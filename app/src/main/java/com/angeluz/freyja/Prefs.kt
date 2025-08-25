package com.angeluz.freyja

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// SpeakMode vive en app/src/main/java/com/angeluz/freyja/SpeakMode.kt

object Prefs {
    // Modo de habla
    private val _speakModeFlow = MutableStateFlow(SpeakMode.OFF)
    val speakModeFlow: StateFlow<SpeakMode> = _speakModeFlow
    fun setSpeakMode(mode: SpeakMode) { _speakModeFlow.value = mode }

    // Estado de "desbloqueado"
    private val _unlocked = MutableStateFlow(false)
    val isUnlocked: StateFlow<Boolean> = _unlocked

    // Lecturas/sets sin y con Context para compatibilidad
    fun isUnlocked(): Boolean = _unlocked.value
    fun isUnlocked(ctx: Context): Boolean = _unlocked.value

    fun setUnlocked(v: Boolean) { _unlocked.value = v }
    fun setUnlocked(ctx: Context, v: Boolean) { _unlocked.value = v }
}
