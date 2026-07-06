package com.kmp.pyr.inner.controller

import com.kmp.pyr.AbOverride
import com.kmp.pyr.inner.model.LoadingVariant
import kotlin.random.Random

/**
 * Assigns the session to a [com.kmp.pyr.inner.model.LoadingVariant] and persists it via [com.kmp.pyr.inner.data.LoadingPrefs], so a user keeps the
 * same variant across launches. Also builds the analytics query string with the configured keys.
 */
class LoadingTestController internal constructor(
    private val prefs: com.kmp.pyr.inner.data.LoadingPrefs,
    private val config: com.kmp.pyr.inner.config.LoadingSdkConfig,
) {
    /** Returns the assigned variant, assigning (and persisting) a random one on first call. */
    fun getVariant(): com.kmp.pyr.inner.model.LoadingVariant {
        if (AbOverride.forceDisabled) return LoadingVariant.VARIANT_ONE
        prefs.getVariantId()?.let { return _root_ide_package_.com.kmp.pyr.inner.model.LoadingVariant.Companion.fromId(it) }
        val randomId = Random.nextInt(1, 4)
        prefs.saveVariantId(randomId)
        return _root_ide_package_.com.kmp.pyr.inner.model.LoadingVariant.Companion.fromId(randomId)
    }

    /**
     * Returns the test parameters as a query string.
     * Format: "{extra3Key}=loading&{extra4Key}={variantId}" (default: "extra_3=loading&extra_4=N").
     * The "loading" value is fixed and cannot be configured.
     */
    fun getLoadingVariantValue(): String {
        val variantId = getVariant().id
        return variantId.toString()
    }

    /** Reassigns a fresh random variant, always different from the current one (for testing / cycling). */
    fun resetVariant() {
        val current = prefs.getVariantId()
        val next = LoadingVariant.entries
            .map { it.id }
            .filter { it != current }
            .random()
        prefs.saveVariantId(next)
    }

    companion object {
        /** Fixed value of the [com.kmp.pyr.inner.config.LoadingSdkConfig.extra3Key] flag. */
        const val EXTRA_3_VALUE = "loading"
    }
}
