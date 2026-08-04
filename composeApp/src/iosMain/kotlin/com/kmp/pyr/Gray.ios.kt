package com.kmp.pyr

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.kmp.pyr.connectivity.ConnectivityGate
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import platform.Network.nw_path_get_status
import platform.Network.nw_path_monitor_cancel
import platform.Network.nw_path_monitor_create
import platform.Network.nw_path_monitor_set_queue
import platform.Network.nw_path_monitor_set_update_handler
import platform.Network.nw_path_monitor_start
import platform.Network.nw_path_status_satisfied
import platform.darwin.dispatch_queue_create

//@Composable
//actual fun Gray(
//    loading: @Composable (() -> Unit),
//    noInternet: @Composable ((onRetry: () -> Unit) -> Unit),
//    white: @Composable (() -> Unit)
//) {
//    val connectivity = remember { connectivityFlow() }
//    // iOS has no system back to block; the gate itself enforces the flow.
//    ConnectivityGate(
//        connectivity = connectivity,
//        loading = loading,
//        noInternet = noInternet,
//        white = white,
//    )
//}

@OptIn(ExperimentalForeignApi::class)
private fun connectivityFlow(): Flow<Boolean> = callbackFlow {
    val monitor = nw_path_monitor_create()
    nw_path_monitor_set_update_handler(monitor) { path ->
        trySend(nw_path_get_status(path) == nw_path_status_satisfied)
    }
    val queue = dispatch_queue_create("com.kmp.pyr.connectivity", null)
    nw_path_monitor_set_queue(monitor, queue)
    nw_path_monitor_start(monitor)
    awaitClose { nw_path_monitor_cancel(monitor) }
}
