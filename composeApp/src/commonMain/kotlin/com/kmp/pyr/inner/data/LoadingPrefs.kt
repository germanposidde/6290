package com.kmp.pyr.inner.data

/**
 * Persistent key-value storage for the SDK's variant assignment. Implemented per platform
 * (SharedPreferences on Android, NSUserDefaults on iOS) so the assigned variant survives app
 * launches.
 */
interface LoadingPrefs {
    /** Returns the saved variant id, or null if none has been assigned yet. */
    fun getVariantId(): Int?

    /** Persists the assigned variant id. */
    fun saveVariantId(id: Int)

    companion object {
        const val PREFS_NAME = "loading_sdk_prefs"
        const val KEY_VARIANT_ID = "variant_id"
    }
}
