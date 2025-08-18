package com.angeluz.freyja

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val DS_NAME = "freyja_prefs"

val Context.dataStore by preferencesDataStore(name = DS_NAME)

enum class SpeakMode { TERMUX, REMOTE, NATIVE }

object Prefs {
    private val KEY_MODE = stringPreferencesKey("mode")
    private val KEY_URL = stringPreferencesKey("server_url")
    private val KEY_TOKEN = stringPreferencesKey("auth_token")

    fun modeFlow(ctx: Context): Flow<SpeakMode> =
        ctx.dataStore.data.map { p ->
            runCatching { SpeakMode.valueOf(p[KEY_MODE] ?: "TERMUX") }
                .getOrDefault(SpeakMode.TERMUX)
        }

    suspend fun setMode(ctx: Context, mode: SpeakMode) {
        ctx.dataStore.edit { it[KEY_MODE] = mode.name }
    }

    fun urlFlow(ctx: Context): Flow<String> =
        ctx.dataStore.data.map { it[KEY_URL] ?: "" }

    suspend fun setUrl(ctx: Context, url: String) {
        ctx.dataStore.edit { it[KEY_URL] = url }
    }

    fun tokenFlow(ctx: Context): Flow<String> =
        ctx.dataStore.data.map { it[KEY_TOKEN] ?: "" }

    suspend fun setToken(ctx: Context, token: String) {
        ctx.dataStore.edit { it[KEY_TOKEN] = token }
    }
}