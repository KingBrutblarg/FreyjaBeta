package com.angeluz.freyja

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Nombre del DataStore
private const val DS_NAME = "freyja_prefs"

// Delegado DataStore en Context
val Context.dataStore by preferencesDataStore(name = DS_NAME)

// Modos de “habla” / invocación (ajústalo a tu lógica)
enum class SpeakMode { TERMUX, REMOTE, NATIVE }

object Pref {

    // ---- Claves ----
    private val KEY_MODE        = stringPreferencesKey("mode")
    private val KEY_URL         = stringPreferencesKey("server_url")
    private val KEY_TOKEN       = stringPreferencesKey("auth_token")
    private val KEY_LAST_STATUS = stringPreferencesKey("last_status")

    // ---- Flows (lecturas reactivas) ----
    fun modeFlow(ctx: Context): Flow<SpeakMode> =
        ctx.dataStore.data.map { p ->
            runCatching { SpeakMode.valueOf(p[KEY_MODE] ?: "TERMUX") }
                .getOrDefault(SpeakMode.TERMUX)
        }

    fun urlFlow(ctx: Context): Flow<String> =
        ctx.dataStore.data.map { it[KEY_URL] ?: "" }

    fun tokenFlow(ctx: Context): Flow<String> =
        ctx.dataStore.data.map { it[KEY_TOKEN] ?: "" }

    fun lastStatusFlow(ctx: Context): Flow<String?> =
        ctx.dataStore.data.map { it[KEY_LAST_STATUS] }

    // ---- Escrituras (suspend) ----
    suspend fun setMode(ctx: Context, mode: SpeakMode) {
        ctx.dataStore.edit { it[KEY_MODE] = mode.name }
    }

    suspend fun setUrl(ctx: Context, url: String) {
        ctx.dataStore.edit { it[KEY_URL] = url }
    }

    suspend fun setToken(ctx: Context, token: String) {
        ctx.dataStore.edit { it[KEY_TOKEN] = token }
    }

    suspend fun setLastStatus(ctx: Context, value: String?) {
        ctx.dataStore.edit { prefs ->
            if (value == null) prefs.remove(KEY_LAST_STATUS)
            else prefs[KEY_LAST_STATUS] = value
        }
    }
}