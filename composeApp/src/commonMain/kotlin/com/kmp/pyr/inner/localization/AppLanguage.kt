package com.kmp.pyr.inner.localization

/**
 * Languages the loading screen is localized into. [EN] is the default / fallback for any
 * unsupported device language.
 *
 * Note: Japanese uses the ISO 639-1 code `ja`; the legacy/user-facing `jp` is mapped to it.
 */
enum class AppLanguage(val code: String) {
    EN("en"),
    ES("es"),
    FR("fr"),
    DE("de"),
    IT("it"),
    KO("ko"),
    JA("ja");

    companion object {
        /**
         * Resolves a language tag/code (e.g. "en", "es-ES", "ja", or legacy "jp") to a supported
         * [AppLanguage], defaulting to [EN] when there's no match.
         */
        fun fromCode(code: String): AppLanguage {
            val normalized = code.lowercase().substringBefore('-').substringBefore('_')
            return when (normalized) {
                "jp" -> JA
                else -> entries.firstOrNull { it.code == normalized } ?: EN
            }
        }
    }
}
