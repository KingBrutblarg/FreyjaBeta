package com.angeluz.freyja

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val DS_NAME = "freyja_prefs"

private val Context.dataStore by preferencesDataStore(DS_NAME)

object Prefs {
    private val KEY_UNLOCKED = stringPreferencesKey("unlocked")
    private val KEY_MODE     = stringPreferencesKey("speak_mode")

    // Flow de desbloqueo (default: false)
    val Context.isUnlockedFlow: Flow<Boolean>
        get() = dataStore.data.map { it[KEY_UNLOCKED] == "1" }

    // Flow de modo de voz (default: OFF)
    val Context.speakModeFlow: Flow<SpeakMode>
        get() = dataStore.data.map { prefs ->
            when (prefs[KEY_MODE]) {
                "PUSH_TO_TALK" -> SpeakMode.PUSH_TO_TALK
                "WAKE_WORD"    -> SpeakMode.WAKE_WORD
                else           -> SpeakMode.OFF
            }
        }

    suspend fun setUnlocked(context: Context, unlocked: Boolean) {
        context.dataStore.edit { it[KEY_UNLOCKED] = if (unlocked) "1" else "0" }
    }

    suspend fun setSpeakMode(context: Context, mode: SpeakMode) {
        context.dataStore.edit { it[KEY_MODE] = mode.name }
    }
}
