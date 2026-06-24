package com.kmp.pyr.connectivity

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

internal enum class GatePhase { Loading, Online, Offline }

/**
 * Connectivity entry gate shared by both platforms. The Loading screen is the
 * only doorway into the app: cold start, Retry, and an automatic reconnect that
 * arrives while offline all route through Loading (with its minimum dwell) before
 * the app is revealed.
 *
 * [connectivity] is a hot/cold flow emitting `true` when the network is reachable.
 * Platform [Gray] actuals supply it (Android NetworkCallback / iOS NWPathMonitor)
 * and wrap [loading]/[noInternet] with their own back-blocking.
 */
@Composable
fun ConnectivityGate(
    connectivity: Flow<Boolean>,
    loading: @Composable () -> Unit,
    noInternet: @Composable (onRetry: () -> Unit) -> Unit,
    white: @Composable () -> Unit,
    minDwellMs: Long = 1000L,
) {
    var phase by remember { mutableStateOf(GatePhase.Loading) }
    var attempt by remember { mutableStateOf(0) }

    LaunchedEffectKeyed(attempt) {
        phase = GatePhase.Loading
        val dwell = launch { delay(minDwellMs) }
        connectivity.distinctUntilChanged().collect { online ->
            if (online) {
                if (phase == GatePhase.Offline) {
                    phase = GatePhase.Loading
                    delay(minDwellMs)
                } else {
                    dwell.join()
                }
                phase = GatePhase.Online
            } else {
                if (phase == GatePhase.Loading) dwell.join()
                phase = GatePhase.Offline
            }
        }
    }

    Crossfade(targetState = phase, animationSpec = tween(350), label = "gate") { p ->
        when (p) {
            GatePhase.Loading -> loading()
            GatePhase.Offline -> noInternet { attempt++ }
            GatePhase.Online -> white()
        }
    }
}

/** Tiny alias so the keyed effect reads clearly at the call site. */
@Composable
private fun LaunchedEffectKeyed(key: Any?, block: suspend kotlinx.coroutines.CoroutineScope.() -> Unit) {
    androidx.compose.runtime.LaunchedEffect(key, block)
}
