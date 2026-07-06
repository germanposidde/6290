package com.kmp.pyr.inner.controller

import kotlinx.coroutines.delay
import kotlin.time.ComparableTimeMark
import kotlin.time.TimeSource

/**
 * Ensures the loading screen is displayed for at least [minDurationMs], even when the underlying
 * data loads faster. The duration is set per variant (only one test uses [MIN_DURATION_MS]; the
 * rest pass 0 and the gate becomes a no-op).
 *
 * Uses the multiplatform monotonic clock instead of `System.currentTimeMillis()`.
 */
class MinDurationController internal constructor(
    private val minDurationMs: Long = MIN_DURATION_MS,
) {
    private var startMark: ComparableTimeMark? = null

    /** Call this when loading starts. */
    fun start() {
        startMark = TimeSource.Monotonic.markNow()
    }

    /**
     * Waits, if needed, so the loading screen has been visible for at least [minDurationMs].
     * Returns immediately if [minDurationMs] is 0 or the load already took longer than the minimum.
     */
    suspend fun awaitMinDuration() {
        val mark = startMark ?: return
        val remaining = minDurationMs - mark.elapsedNow().inWholeMilliseconds
        if (remaining > 0) delay(remaining)
    }

    companion object {
        /** Minimum duration for the 3s test variant. */
        const val MIN_DURATION_MS: Long = 3000L
    }
}
