package com.kmp.pyr

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import kotlin.Int

@Composable
@Preview
fun App() {
    Gray(
        loading = { LoadingScreen() },
        noInternet = { NoInternetScreen(it) },
        white = { AppNavGraph() }
    )
}