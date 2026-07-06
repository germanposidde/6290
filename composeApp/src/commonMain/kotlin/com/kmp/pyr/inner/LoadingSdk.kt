package com.kmp.pyr.inner
import com.kmp.pyr.inner.config.LoadingSdkConfig
import com.kmp.pyr.inner.controller.LoadingTestController
import com.kmp.pyr.inner.controller.MinDurationController
import com.kmp.pyr.inner.data.LoadingPrefs
import com.kmp.pyr.inner.model.LoadingVariant
import kotlin.concurrent.Volatile

object LoadingSdk {
    @Volatile
    private var controller: LoadingTestController? = null

    @Volatile
    private var sdkConfig: LoadingSdkConfig? = null

    @Volatile
    private var minDurationController: MinDurationController? = null

    fun init(prefs: LoadingPrefs, config: LoadingSdkConfig = LoadingSdkConfig.default()) {
        if (controller != null) return
        controller =
            LoadingTestController(
                prefs,
                config
            )
        sdkConfig = config
        controller?.getVariant()
    }

    fun getController(): LoadingTestController = controller
        ?: error("LoadingSdk not initialized. Call LoadingSdk.init(...) first.")

    fun getConfig(): LoadingSdkConfig = sdkConfig
        ?: error("LoadingSdk not initialized. Call LoadingSdk.init(...) first.")

    fun getLoadingValue(): String = getController().getLoadingVariantValue()

    fun createMinDurationController(): MinDurationController {
        val minDurationMs = when (getController().getVariant()) {
            LoadingVariant.VARIANT_ONE -> MinDurationController.MIN_DURATION_MS
            else -> 0L
        }
        return MinDurationController(
            minDurationMs
        )
    }

    /**
     * Starts (and stores) the min-duration gate. Call once when loading begins (e.g. when the
     * Gray block starts). Returns the controller in case the caller wants a direct reference.
     */
    fun startMinDuration(): MinDurationController =
        createMinDurationController().apply { start() }
            .also { minDurationController = it }

    /**
     * Suspends until the minimum duration started by [startMinDuration] has elapsed. Returns
     * immediately if it was never started. Call this from any other block right before continuing
     * (e.g. before showing the WebView).
     */
    suspend fun awaitMinDuration() {
        minDurationController?.awaitMinDuration()
    }
}
