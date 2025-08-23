package com.angeluz.freyja

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension DataStore ligada al Context (nombre del almacén)
val Context.freyjaDataStore by preferencesDataStore(name = "freyja_prefs")

class Prefs(private val context: Context) {

    private companion object {
        val KEY_TOKEN = stringPreferencesKey("token")
        val KEY_USER  = stringPreferencesKey("user")
        val KEY_THEME = stringPreferencesKey("theme")
        val KEY_RUNE  = stringPreferencesKey("rune")
    }

    // Lecturas como Flow<String?>
    val token: Flow<String?> = context.freyjaDataStore.data.map { prefs -> prefs[KEY_TOKEN] }
    val user:  Flow<String?> = context.freyjaDataStore.data.map { prefs -> prefs[KEY_USER] }
    val theme: Flow<String?> = context.freyjaDataStore.data.map { prefs -> prefs[KEY_THEME] }
    val rune:  Flow<String?> = context.freyjaDataStore.data.map { prefs -> prefs[KEY_RUNE] }

    // Escrituras (suspend)
    suspend fun setToken(value: String?) = context.freyjaDataStore.edit { prefs ->
        if (value == null) prefs.remove(KEY_TOKEN) else prefs[KEY_TOKEN] = value
    }
    suspend fun setUser(value: String?) = context.freyjaDataStore.edit { prefs ->
        if (value == null) prefs.remove(KEY_USER) else prefs[KEY_USER] = value
    }
    suspend fun setTheme(value: String?) = context.freyjaDataStore.edit { prefs ->
        if (value == null) prefs.remove(KEY_THEME) else prefs[KEY_THEME] = value
    }
    suspend fun setRune(value: String?) = context.freyjaDataStore.edit { prefs ->
        if (value == null) prefs.remove(KEY_RUNE) else prefs[KEY_RUNE] = value
    }
}
