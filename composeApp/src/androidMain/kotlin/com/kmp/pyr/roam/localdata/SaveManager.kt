package com.kmp.pyr.roam.localdata

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.kmp.pyr.roam.util.tokenToSave
import kotlinx.coroutines.flow.first

private val Context.saveDataStore by preferencesDataStore(name = "ma")

object SaveManager {
    private val KEY_STRING = stringPreferencesKey("mi")

    suspend fun safeSave(context: Context, value: String) {
        val dataStore = context.saveDataStore

        val existing = dataStore.data.first()[KEY_STRING]
        if (existing != null) {
            throw IllegalStateException("Value already saved: $existing")
        }

        tokenToSave()
        dataStore.edit { prefs ->
            prefs[KEY_STRING] = value
        }
    }

    suspend fun getData(context: Context): String {
        val dataStore = context.saveDataStore
        return dataStore.data.first()[KEY_STRING] ?: ""
    }
}
