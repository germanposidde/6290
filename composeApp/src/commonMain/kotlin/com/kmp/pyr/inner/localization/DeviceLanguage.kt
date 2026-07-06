package com.kmp.pyr.inner.localization

import com.kmp.pyr.deviceLanguageCode


/** The device's current [AppLanguage], falling back to [AppLanguage.EN] when unsupported. */
fun currentAppLanguage(): AppLanguage = AppLanguage.fromCode(deviceLanguageCode())
