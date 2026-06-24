package com.kmp.pyr.data

import com.russhwolf.settings.Settings
import kotlinx.serialization.json.Json

/** Stores the whole kingdom as one JSON blob in platform key-value storage. */
class Persistence(private val settings: Settings = Settings()) {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        prettyPrint = false
    }

    fun load(): KingdomSnapshot? {
        val raw = settings.getStringOrNull(KEY) ?: return null
        return runCatching { json.decodeFromString(KingdomSnapshot.serializer(), raw) }.getOrNull()
    }

    fun save(snapshot: KingdomSnapshot) {
        settings.putString(KEY, json.encodeToString(KingdomSnapshot.serializer(), snapshot))
    }

    /** Pretty JSON for the backup/export preview. */
    fun export(snapshot: KingdomSnapshot): String =
        prettyJson.encodeToString(KingdomSnapshot.serializer(), snapshot)

    /** Parse a pasted backup; returns null if invalid. */
    fun import(text: String): KingdomSnapshot? =
        runCatching { prettyJson.decodeFromString(KingdomSnapshot.serializer(), text) }.getOrNull()

    fun clear() = settings.remove(KEY)

    private val prettyJson = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        prettyPrint = true
    }

    companion object {
        private const val KEY = "kingdom_snapshot_v1"
    }
}
