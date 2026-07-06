package com.kmp.pyr

import platform.Foundation.NSLocale
import platform.Foundation.preferredLanguages

actual fun deviceLanguageCode(): String =
    (NSLocale.preferredLanguages.firstOrNull() as? String) ?: "en"