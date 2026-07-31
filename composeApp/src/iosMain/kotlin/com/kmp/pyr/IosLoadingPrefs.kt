package com.kmp.pyr

import com.kmp.pyr.inner.LoadingSdk
import com.kmp.pyr.inner.config.LoadingSdkConfig
import com.kmp.pyr.inner.data.LoadingPrefs
import platform.Foundation.NSUserDefaults

class IosLoadingPrefs : LoadingPrefs {
    private val defaults = NSUserDefaults.standardUserDefaults

    private val key = "${LoadingPrefs.PREFS_NAME}.${LoadingPrefs.KEY_VARIANT_ID}"

    override fun getVariantId(): Int? =
        if (defaults.objectForKey(key) != null) defaults.integerForKey(key).toInt() else null

    override fun saveVariantId(id: Int) {
        defaults.setInteger(id.toLong(), key)
    }
}

fun LoadingSdk.init(config: LoadingSdkConfig = LoadingSdkConfig.default()) {
    init(IosLoadingPrefs(), config)
}
