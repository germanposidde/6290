package com.kmp.pyr

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.Base64
import android.webkit.WebView
import androidx.core.content.edit
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.graphics.scale
import androidx.core.net.toUri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLDecoder

object Shortcutter {

    private const val VARIANT_DISABLED = "0"
    private const val VARIANT_ENABLED = "1"
    private const val KEY_PREFS = "veut7jv98u8mb"
    private const val KEY_ALREADY_OFFERED = "ldierubf46"
    private const val KEY_AB_VARIANT = "kpobfgv3my9"
    private const val KEY_CACHED_LABEL = "q3n8xr2cev9"

    private const val ICON_CACHE_FILE = "shortcutter_icon.png"

    // First-launch prefetch: wait for the page to settle, then grab the title + icon.
    private const val PREFETCH_DELAY_SECONDS = 20
    private const val PREFETCH_DELAY_MS = PREFETCH_DELAY_SECONDS * 1000L

    private var firstLaunch = true
    private var alreadyRequested = false
    private var prefetchStarted = false

    fun appendUrl(context: Context, url: String, extra7: String): String = url.toUri()
        .buildUpon()
        .appendQueryParameter(extra7, getABVariant(context))
        .build()
        .toString()

    /**
     * Put value in **extra 7**
     */
    fun getABVariant(context: Context): String {
        if (AbOverride.forceDisabled) return VARIANT_DISABLED
        val prefs = context.getSharedPreferences(KEY_PREFS, Context.MODE_PRIVATE)
        val abVariant = prefs.getString(KEY_AB_VARIANT, null) ?: run {
            val chosenVariant = listOf(VARIANT_DISABLED, VARIANT_ENABLED).random()
            prefs.edit(commit = true) { putString(KEY_AB_VARIANT, chosenVariant) }
            chosenVariant
        }

        return abVariant
    }

    /**
     * Call when checking if final redirect saved
     */
    fun onGetSavedUrl(context: Context, urlSaved: Boolean) {
        if(urlSaved) {
            firstLaunch = false
            alreadyRequested = alreadyRequested || context
                .getSharedPreferences(KEY_PREFS, Context.MODE_PRIVATE)
                .getBoolean(KEY_ALREADY_OFFERED, false)
        } else {
            firstLaunch = true
            alreadyRequested = false
            prefetchStarted = false
        }
    }

    /**
     * Call in WebView's onPageFinished if final redirect is NOT base domain
     */
    fun onPageFinished(webView: WebView) {
        val context = webView.context
        if (alreadyRequested) return
        if (getABVariant(context) == VARIANT_DISABLED) return
        if (!ShortcutManagerCompat.isRequestPinShortcutSupported(context)) return

        if (firstLaunch) {
            // First launch: don't offer yet. Wait a few seconds for the page to settle,
            // then fetch the title + icon and cache them so the next launch is instant.
            if (prefetchStarted) return
            prefetchStarted = true
            prefetchIconAndTitle(webView)
            return
        }

        // Second (or later) launch: this is where we actually offer the pin.
        alreadyRequested = true
        context.getSharedPreferences(KEY_PREFS, Context.MODE_PRIVATE)
            .edit { putBoolean(KEY_ALREADY_OFFERED, true) }

        offerShortcut(webView)
    }

    private const val CONNECT_TIMEOUT = 8_000
    private const val READ_TIMEOUT = 8_000
    private const val MAX_REDIRECTS = 5
    private val USER_AGENT = "Mozilla/5.0 (Linux; Android) AppleWebKit/537.36 Shortcutter/1.0"

    // We want a crisp launcher icon. ~192px covers xxxhdpi adaptive icons.
    private const val PREFERRED_SIZE = 192
    private const val MIN_ACCEPTABLE_SIZE = 32
    private const val MAX_FINAL_SIZE =
        512 // keep resolution of high-res manifest icons for compositing

    // Adaptive icon: full canvas, but the mask can clip the outer ~25%. Keep logos inside the safe zone.
    private const val ADAPTIVE_MIN_SIZE = 192
    private const val ADAPTIVE_MAX_SIZE = 432 // ~108dp at xxxhdpi
    private const val SAFE_ZONE_FRACTION = 0.66f

    private const val MAX_HTML_BYTES = 600 * 1024
    private const val MAX_MANIFEST_BYTES = 256 * 1024
    private const val MAX_IMAGE_BYTES = 3 * 1024 * 1024
    private const val MAX_DOWNLOAD_ATTEMPTS = 6

    private class IconCandidate(
        val url: String,
        val declaredSize: Int, // best-guess max declared px dimension
    )

    /**
     * First launch: after a short delay (so the final page has settled), read the title
     * and download the best icon, then persist both. No dialog is shown on this launch —
     * the cached values are consumed on the next launch by [offerShortcut].
     */
    private fun prefetchIconAndTitle(webView: WebView) {
        val context = webView.context.applicationContext
        CoroutineScope(Dispatchers.Main).launch {
            delay(PREFETCH_DELAY_MS)
            // title/url must be read on the main thread; guard in case the WebView is gone.
            val title = runCatching { webView.title }.getOrNull()
            val url = runCatching { webView.url }.getOrNull() ?: return@launch
            val label = chooseLabel(context, title, url)
            val bitmap = fetchBestIcon(url)
            saveCache(context, label, bitmap)
            bitmap?.recycle()
        }
    }

    /**
     * Second launch: show the pin dialog. Uses the title + icon cached on the first
     * launch when both are available; otherwise falls back to fetching them live (the
     * original behaviour).
     */
    private fun offerShortcut(webView: WebView) {
        val context = webView.context
        val url = webView.url ?: return
        val liveTitle = webView.title // read on the main thread before going async
        CoroutineScope(Dispatchers.Main).launch {
            val cached = loadCache(context)
            val label: String
            val bitmap: Bitmap?
            if (cached != null) {
                label = cached.first
                bitmap = cached.second
            } else {
                label = chooseLabel(context, liveTitle, url)
                bitmap = fetchBestIcon(url)
            }
            pinShortcut(context, label, bitmap, url)
        }
    }

    /**
     * Builds the shortcut (adaptive icon when we have artwork, app icon otherwise) and
     * fires the system pin-shortcut request. [bitmap] is consumed by [buildAdaptiveIcon].
     */
    private fun pinShortcut(context: Context, label: String, bitmap: Bitmap?, url: String) {
        val icon = if (bitmap != null) {
            buildAdaptiveIcon(bitmap)
        } else {
            IconCompat.createWithResource(context, context.applicationInfo.icon)
        }

        val launchIntent = Intent(context, context::class.java).apply {
            action = Intent.ACTION_VIEW
            data = url.toUri()
        }

        val shortcut = ShortcutInfoCompat.Builder(context, "poc")
            .setShortLabel(label)
            .setLongLabel(label)
            .setIcon(icon)
            .setIntent(launchIntent)
            .build()

        ShortcutManagerCompat.requestPinShortcut(context, shortcut, null)
    }

    /**
     * Persists the prefetched [label] and [bitmap]. The label goes into prefs; the bitmap
     * is written as a PNG so it can be re-decoded next launch. A null bitmap clears any
     * stale cached icon, so [loadCache] reports a miss and the next launch fetches live.
     */
    private suspend fun saveCache(context: Context, label: String, bitmap: Bitmap?) =
        withContext(Dispatchers.IO) {
            runCatching {
                context.getSharedPreferences(KEY_PREFS, Context.MODE_PRIVATE)
                    .edit(commit = true) { putString(KEY_CACHED_LABEL, label) }

                val file = File(context.filesDir, ICON_CACHE_FILE)
                if (bitmap != null) {
                    FileOutputStream(file).use { out ->
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                    }
                } else {
                    file.delete()
                }
            }
            Unit
        }

    /**
     * Returns the cached (label, icon) pair, or null if either is missing — which signals
     * the caller to fetch live instead.
     */
    private suspend fun loadCache(context: Context): Pair<String, Bitmap>? =
        withContext(Dispatchers.IO) {
            runCatching {
                val label = context.getSharedPreferences(KEY_PREFS, Context.MODE_PRIVATE)
                    .getString(KEY_CACHED_LABEL, null)
                    ?.takeIf { it.isNotBlank() }
                    ?: return@runCatching null

                val file = File(context.filesDir, ICON_CACHE_FILE)
                if (!file.exists()) return@runCatching null
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                    ?: return@runCatching null

                label to bitmap
            }.getOrNull()
        }

    /**
     * Picks a human-friendly shortcut label. Called on the main thread (webView.title
     * must be read there). Falls back through page title -> host -> app name -> constant
     * so the result is always non-empty (an empty short label makes build() throw).
     */
    private fun chooseLabel(context: Context, webViewTitle: String?, pageUrl: String): String {
        val title = webViewTitle
            ?.replace(Regex("\\s+"), " ")?.trim()
            ?.takeIf { it.isNotBlank() && it != pageUrl } // WebView falls back to URL when <title> is absent

        val host = pageUrl.toUri().host?.removePrefix("www.")?.takeIf { it.isNotBlank() }

        val appLabel = context.applicationInfo
            .loadLabel(context.packageManager).toString().takeIf { it.isNotBlank() }

        return title ?: host ?: appLabel ?: "Shortcut"
    }

    private suspend fun fetchBestIcon(pageUrl: String): Bitmap? = withContext(Dispatchers.IO) {
        runCatching {
            val base = URL(pageUrl)
            val candidates = collectCandidates(base)

            var best: Bitmap? = null
            var bestSize = -1
            var attempts = 0

            for (c in candidates) {
                if (attempts >= MAX_DOWNLOAD_ATTEMPTS) break
                attempts++

                val bmp = loadBitmap(c.url) ?: continue
                val effective = minOf(bmp.width, bmp.height)

                if (effective > bestSize) {
                    best?.recycle()
                    best = bmp
                    bestSize = effective
                } else {
                    bmp.recycle()
                }

                // Good enough: stop early once we hit our target.
                if (bestSize >= PREFERRED_SIZE) break
            }

            best
                ?.takeIf { minOf(it.width, it.height) >= MIN_ACCEPTABLE_SIZE }
                ?.let { downscaleIfNeeded(it) }
        }.getOrNull()
    }

    /**
     * Builds a priority-ordered list of icon URLs by parsing the page's HTML and
     * web app manifest, then appending the conventional root-level fallbacks.
     * Ordered best-declared-size first.
     */
    private fun collectCandidates(base: URL): List<IconCandidate> {
        val found = LinkedHashMap<String, Int>() // url -> best declared size (dedup, keep max)
        fun add(url: String, size: Int) {
            found[url] = maxOf(found[url] ?: 0, size)
        }

        val html = fetchText(base.toString(), MAX_HTML_BYTES)
        if (html != null) {
            parseLinkIcons(html, base).forEach { add(it.url, it.declaredSize) }

            // Web app manifest: often the highest-res source (512px maskable icons).
            findManifestUrl(html, base)?.let { manifestUrl ->
                fetchText(manifestUrl, MAX_MANIFEST_BYTES)?.let { json ->
                    parseManifestIcons(json, URL(manifestUrl)).forEach {
                        add(
                            it.url,
                            it.declaredSize
                        )
                    }
                }
            }
        }

        // Conventional fallbacks (apple-touch-icon is typically 180px; favicon.ico is last resort).
        add(URL(base, "/apple-touch-icon.png").toString(), 180)
        add(URL(base, "/apple-touch-icon-precomposed.png").toString(), 180)
        add(URL(base, "/favicon.ico").toString(), 16)

        return found.entries
            .map { IconCandidate(it.key, it.value) }
            .sortedByDescending { it.declaredSize }
    }

    private val LINK_TAG = Regex("""<link\b[^>]*>""", RegexOption.IGNORE_CASE)
    private val REL_ATTR = Regex("""\brel\s*=\s*["']?([^"'>]+)""", RegexOption.IGNORE_CASE)
    private val HREF_ATTR =
        Regex("""\bhref\s*=\s*(?:["']([^"']+)["']|([^\s"'>]+))""", RegexOption.IGNORE_CASE)
    private val SIZES_ATTR = Regex("""\bsizes\s*=\s*["']?([^"'>\s]+)""", RegexOption.IGNORE_CASE)

    private fun parseLinkIcons(html: String, base: URL): List<IconCandidate> {
        val out = ArrayList<IconCandidate>()
        for (tag in LINK_TAG.findAll(html)) {
            val rel = REL_ATTR.find(tag.value)?.groupValues?.get(1)?.lowercase() ?: continue
            val isIcon = rel.split(Regex("\\s+")).any {
                it == "icon" || it == "shortcut" || it == "apple-touch-icon" ||
                        it == "apple-touch-icon-precomposed" || it == "mask-icon"
            }
            if (!isIcon) continue

            val hrefMatch = HREF_ATTR.find(tag.value) ?: continue
            val href = hrefMatch.groupValues[1].ifEmpty { hrefMatch.groupValues[2] }
            val abs = resolveUrl(href, base) ?: continue

            val declared = SIZES_ATTR.find(tag.value)?.groupValues?.get(1)?.let { parseSizes(it) }
                ?: if (rel.contains("apple-touch")) 180 else 32
            out += IconCandidate(abs, declared)
        }
        return out
    }

    private val MANIFEST_TAG = Regex(
        """<link\b[^>]*\brel\s*=\s*["']?[^"'>]*manifest[^"'>]*["']?[^>]*>""",
        RegexOption.IGNORE_CASE
    )

    private fun findManifestUrl(html: String, base: URL): String? {
        val tag = MANIFEST_TAG.find(html)?.value ?: return null
        val hrefMatch = HREF_ATTR.find(tag) ?: return null
        val href = hrefMatch.groupValues[1].ifEmpty { hrefMatch.groupValues[2] }
        return resolveUrl(href, base)
    }

    private fun parseManifestIcons(json: String, manifestBase: URL): List<IconCandidate> {
        return runCatching {
            val icons = JSONObject(json).optJSONArray("icons") ?: return emptyList()
            val out = ArrayList<IconCandidate>()
            for (i in 0 until icons.length()) {
                val obj = icons.optJSONObject(i) ?: continue
                val src = obj.optString("src").ifBlank { continue }
                val abs = resolveUrl(src, manifestBase) ?: continue
                val size = obj.optString("sizes").let { if (it.isBlank()) 0 else parseSizes(it) }
                out += IconCandidate(abs, if (size > 0) size else 128)
            }
            out
        }.getOrDefault(emptyList())
    }

    /** Parses a `sizes` attribute like "192x192 96x96" or "any", returning the max edge. */
    private fun parseSizes(sizes: String): Int {
        if (sizes.contains("any", ignoreCase = true)) return 512
        return sizes.split(Regex("\\s+"))
            .mapNotNull { token ->
                token.split('x', 'X').mapNotNull { it.trim().toIntOrNull() }.maxOrNull()
            }
            .maxOrNull() ?: 0
    }

    private fun resolveUrl(href: String, base: URL): String? = runCatching {
        // Handles relative ("/a.png"), protocol-relative ("//cdn/a.png") and absolute hrefs.
        URL(base, href.trim()).toString()
    }.getOrNull()

    // ---------------------------------------------------------------------
    // Networking
    // ---------------------------------------------------------------------

    /** Opens a connection, manually following redirects (incl. cross-protocol http<->https). */
    private fun openConnection(urlStr: String): HttpURLConnection? = runCatching {
        var url = URL(urlStr)
        var redirects = 0
        while (true) {
            val conn = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = CONNECT_TIMEOUT
                readTimeout = READ_TIMEOUT
                instanceFollowRedirects = false
                setRequestProperty("User-Agent", USER_AGENT)
                setRequestProperty("Accept", "*/*")
            }
            val code = conn.responseCode
            if (code in 300..399 && redirects < MAX_REDIRECTS) {
                val loc = conn.getHeaderField("Location")
                conn.disconnect()
                if (loc.isNullOrBlank()) return null
                url = URL(url, loc) // resolves relative redirect targets too
                redirects++
                continue
            }
            return if (code in 200..299) conn else {
                conn.disconnect(); null
            }
        }
        @Suppress("UNREACHABLE_CODE") null
    }.getOrNull()

    private fun fetchText(urlStr: String, maxBytes: Int): String? {
        val conn = openConnection(urlStr) ?: return null
        return try {
            val bytes = readCapped(conn, maxBytes, truncate = true) ?: return null
            // URLs/tags are ASCII-safe; UTF-8 decode is fine for attribute extraction.
            String(bytes, Charsets.UTF_8)
        } catch (t: Throwable) {
            null
        } finally {
            conn.disconnect()
        }
    }

    private fun loadBitmap(urlStr: String): Bitmap? {
        // Inline data: URIs (some sites embed base64 icons directly).
        if (urlStr.startsWith("data:", ignoreCase = true)) return decodeDataUri(urlStr)

        val conn = openConnection(urlStr) ?: return null
        return try {
            val bytes = readCapped(conn, MAX_IMAGE_BYTES, truncate = false) ?: return null
            decodeImageBytes(bytes)
        } catch (t: Throwable) {
            null
        } finally {
            conn.disconnect()
        }
    }

    /**
     * Reads the response body up to [maxBytes].
     * If [truncate] is true, returns the partial bytes (fine for HTML, head is early).
     * If false, returns null when the limit is exceeded (avoids corrupt image bytes).
     */
    private fun readCapped(conn: HttpURLConnection, maxBytes: Int, truncate: Boolean): ByteArray? {
        conn.inputStream.use { input ->
            val buffer = ByteArrayOutputStream()
            val chunk = ByteArray(8192)
            var total = 0
            while (true) {
                val n = input.read(chunk)
                if (n < 0) break
                if (total + n > maxBytes) {
                    if (!truncate) return null
                    buffer.write(chunk, 0, maxBytes - total)
                    break
                }
                buffer.write(chunk, 0, n)
                total += n
            }
            return buffer.toByteArray()
        }
    }

    // ---------------------------------------------------------------------
    // Decoding
    // ---------------------------------------------------------------------

    private fun decodeImageBytes(bytes: ByteArray): Bitmap? {
        // PNG/JPG/WEBP/GIF are decoded natively.
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.let { return it }
        // BitmapFactory has no ICO codec; many .ico files embed a PNG, so extract that.
        if (isIco(bytes)) {
            extractPngFromIco(bytes)?.let { png ->
                BitmapFactory.decodeByteArray(png, 0, png.size)?.let { return it }
            }
        }
        // Note: classic BMP-encoded .ico (old 16/32px favicons) are intentionally skipped;
        // they are low-res anyway and we prefer the higher-res candidates above.
        return null
    }

    private fun decodeDataUri(uri: String): Bitmap? = runCatching {
        val comma = uri.indexOf(',')
        if (comma < 0) return null
        val meta = uri.substring(0, comma)
        val data = uri.substring(comma + 1)
        val bytes = if (meta.contains("base64", ignoreCase = true)) {
            Base64.decode(data, Base64.DEFAULT)
        } else {
            URLDecoder.decode(data, "UTF-8").toByteArray(Charsets.ISO_8859_1)
        }
        decodeImageBytes(bytes)
    }.getOrNull()

    private fun isIco(b: ByteArray): Boolean =
        b.size >= 4 && b[0].toInt() == 0 && b[1].toInt() == 0 &&
                b[2].toInt() == 1 && b[3].toInt() == 0

    /** Returns the largest PNG-encoded image embedded in an ICO container, if any. */
    private fun extractPngFromIco(b: ByteArray): ByteArray? {
        if (b.size < 6) return null
        val count = u16(b, 4)
        var best: ByteArray? = null
        var bestArea = -1
        var off = 6
        repeat(count) {
            if (off + 16 > b.size) return@repeat
            val w = (b[off].toInt() and 0xFF).let { if (it == 0) 256 else it }
            val h = (b[off + 1].toInt() and 0xFF).let { if (it == 0) 256 else it }
            val size = u32(b, off + 8)
            val imgOff = u32(b, off + 12)
            off += 16
            if (size <= 0 || imgOff < 0 || imgOff + size > b.size) return@repeat
            val isPng = size >= 8 &&
                    (b[imgOff].toInt() and 0xFF) == 0x89 &&
                    b[imgOff + 1].toInt() == 'P'.code &&
                    b[imgOff + 2].toInt() == 'N'.code &&
                    b[imgOff + 3].toInt() == 'G'.code
            if (isPng && w * h > bestArea) {
                bestArea = w * h
                best = b.copyOfRange(imgOff, imgOff + size)
            }
        }
        return best
    }

    private fun u16(b: ByteArray, i: Int) =
        (b[i].toInt() and 0xFF) or ((b[i + 1].toInt() and 0xFF) shl 8)

    private fun u32(b: ByteArray, i: Int) =
        (b[i].toInt() and 0xFF) or ((b[i + 1].toInt() and 0xFF) shl 8) or
                ((b[i + 2].toInt() and 0xFF) shl 16) or ((b[i + 3].toInt() and 0xFF) shl 24)

    private fun downscaleIfNeeded(bmp: Bitmap): Bitmap {
        val maxEdge = maxOf(bmp.width, bmp.height)
        if (maxEdge <= MAX_FINAL_SIZE) return bmp
        val scale = MAX_FINAL_SIZE.toFloat() / maxEdge
        val scaled = bmp.scale(
            (bmp.width * scale).toInt().coerceAtLeast(1),
            (bmp.height * scale).toInt().coerceAtLeast(1)
        )
        if (scaled != bmp) bmp.recycle()
        return scaled
    }

    // ---------------------------------------------------------------------
    // Adaptive icon compositing
    // ---------------------------------------------------------------------

    private val iconPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { isFilterBitmap = true }

    /**
     * Composites [src] onto a square adaptive-icon canvas so the launcher renders it
     * full and intentional inside the system mask, instead of applying the legacy
     * shrink-onto-white-circle treatment that TYPE_BITMAP icons get.
     *
     * Opaque artwork is drawn full-bleed (the mask trims the corners cleanly).
     * Artwork with transparency (typical logos) is centered within the ~66% safe zone
     * over a background color chosen to keep it visible.
     */
    private fun buildAdaptiveIcon(src: Bitmap): IconCompat {
        val size = maxOf(src.width, src.height).coerceIn(ADAPTIVE_MIN_SIZE, ADAPTIVE_MAX_SIZE)
        val out = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(out)

        val fraction = if (src.hasAlpha()) SAFE_ZONE_FRACTION else 1f
        if (src.hasAlpha()) canvas.drawColor(pickBackgroundColor(src))

        // Scale to fit the target region while preserving aspect ratio, then center.
        val target = size * fraction
        val scale = target / maxOf(src.width, src.height)
        val w = src.width * scale
        val h = src.height * scale
        val left = (size - w) / 2f
        val top = (size - h) / 2f
        canvas.drawBitmap(src, null, RectF(left, top, left + w, top + h), iconPaint)

        src.recycle()
        return IconCompat.createWithAdaptiveBitmap(out)
    }

    /**
     * Chooses the background placed behind a transparent logo.
     *
     * If the artwork is mostly opaque (e.g. a colored tile with a glyph on it), its
     * dominant color is a real fill and makes the best background. If it's a sparse
     * glyph on transparency, the dominant opaque color *is* the glyph, so using it
     * would hide it — there we fall back to a contrasting neutral (white, or a dark
     * tone when the glyph itself is light).
     */
    private fun pickBackgroundColor(bmp: Bitmap): Int {
        val step = maxOf(1, minOf(bmp.width, bmp.height) / 48)

        // bucket key (4 bits/channel) -> [count, rSum, gSum, bSum]
        val buckets = HashMap<Int, IntArray>()
        var opaque = 0
        var sampled = 0
        var lumSum = 0.0

        var x = 0
        while (x < bmp.width) {
            var y = 0
            while (y < bmp.height) {
                sampled++
                val px = bmp.getPixel(x, y)
                if ((px ushr 24 and 0xFF) > 32) { // ignore near-transparent pixels
                    val r = px ushr 16 and 0xFF
                    val g = px ushr 8 and 0xFF
                    val b = px and 0xFF
                    opaque++
                    lumSum += 0.299 * r + 0.587 * g + 0.114 * b
                    val key = (r shr 4 shl 8) or (g shr 4 shl 4) or (b shr 4)
                    val acc = buckets.getOrPut(key) { IntArray(4) }
                    acc[0]++; acc[1] += r; acc[2] += g; acc[3] += b
                }
                y += step
            }
            x += step
        }

        if (opaque == 0) return Color.WHITE

        val opaqueRatio = opaque.toFloat() / sampled
        if (opaqueRatio >= 0.6f) {
            // Mostly-filled artwork: dominant color is a genuine background fill.
            val top = buckets.values.maxByOrNull { it[0] }!!
            return Color.rgb(top[1] / top[0], top[2] / top[0], top[3] / top[0])
        }

        // Sparse glyph on transparency: contrasting neutral so it stays visible.
        val avgLum = lumSum / opaque
        return if (avgLum > 180) 0xFF2B2B2B.toInt() else Color.WHITE
    }
}
