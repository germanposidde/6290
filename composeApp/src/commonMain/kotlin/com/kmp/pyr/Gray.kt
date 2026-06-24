package com.kmp.pyr

import androidx.compose.runtime.Composable

@Composable
expect fun Gray(
    loading: @Composable () -> Unit,
    noInternet: @Composable (onRetry: () -> Unit) -> Unit,
    white: @Composable () -> Unit
)