package com.kmp.pyr

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.kmp.pyr.connectivity.ConnectivityGate
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

@Composable
actual fun Gray(
    loading: @Composable (() -> Unit),
    noInternet: @Composable ((onRetry: () -> Unit) -> Unit),
    white: @Composable (() -> Unit),
    transitionSpec: AnimatedContentTransitionScope<Int>.() -> ContentTransform
) {
    val context = LocalContext.current.applicationContext
    val connectivity = remember(context) { context.connectivityFlow() }

    ConnectivityGate(
        connectivity = connectivity,
        // System back is fully swallowed on both gate screens.
        loading = { BackHandler(enabled = true) {}; loading() },
        noInternet = { retry -> BackHandler(enabled = true) {}; noInternet(retry) },
        white = white,
    )
}

private fun Context.connectivityFlow(): Flow<Boolean> = callbackFlow {
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    fun hasInternet(network: Network?): Boolean {
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    val callback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) { trySend(true) }
        override fun onLost(network: Network) { trySend(hasInternet(cm.activeNetwork)) }
        override fun onCapabilitiesChanged(network: Network, caps: NetworkCapabilities) {
            trySend(caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET))
        }
    }

    trySend(hasInternet(cm.activeNetwork))

    val request = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .build()
    cm.registerNetworkCallback(request, callback)

    awaitClose { cm.unregisterNetworkCallback(callback) }
}
