package com.kmp.pyr

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.kmp.pyr.inner.LoadingSdk
import com.kmp.pyr.inner.config.LoadingSdkConfig
import com.kmp.pyr.inner.data.LoadingPrefs
import java.util.Locale

actual fun deviceLanguageCode(): String = Locale.getDefault().language

class AndroidLoadingPrefs(context: Context) : LoadingPrefs {
    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(LoadingPrefs.PREFS_NAME, Context.MODE_PRIVATE)

    override fun getVariantId(): Int? =
        if (prefs.contains(LoadingPrefs.KEY_VARIANT_ID)) prefs.getInt(LoadingPrefs.KEY_VARIANT_ID, 1) else null

    override fun saveVariantId(id: Int) {
        prefs.edit { putInt(LoadingPrefs.KEY_VARIANT_ID, id) }
    }
}

fun LoadingSdk.init(context: Context, config: LoadingSdkConfig = LoadingSdkConfig.default()) {
    init(AndroidLoadingPrefs(context.applicationContext), config)
}


actual object AbOverride {
    actual val forceDisabled: Boolean = BuildConfig.FORCE_AB_DISABLED
}