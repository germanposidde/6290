package com.kmp.pyr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.kmp.pyr.roam.StartCache
import com.kmp.pyr.roam.localnav.LocalNavObj
import com.kmp.pyr.roam.localnav.ScreenManager

class MainActivity : ComponentActivity() {

    fun hideSystemBars() {
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.systemBars())
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemBars()
    }

    override fun onResume() {
        super.onResume()
        hideSystemBars()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        hideSystemBars()

        val startCache = StartCache(this, intent)
        setContent {
            val screen by LocalNavObj.screen.collectAsState()
            when (screen) {
                ScreenManager.Welcome -> LoadingScreenA(this@MainActivity, startCache)
                ScreenManager.MenuPoint -> App()
                ScreenManager.InternetProblem -> NoInternetScreenA()
                else -> {}
            }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}