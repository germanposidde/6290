package com.kmp.pyr

import androidx.compose.ui.window.ComposeUIViewController
import com.kmp.pyr.inner.LoadingSdk

fun MainViewController() = ComposeUIViewController {
    LoadingSdk.init(loadingConfig())
    App()
}
