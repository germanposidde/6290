package com.kmp.pyr

import android.app.Application
import com.kmp.pyr.inner.LoadingSdk

class App2 : Application() {
    override fun onCreate() {
        super.onCreate()
        LoadingSdk.init(this, loadingConfig())
    }
}