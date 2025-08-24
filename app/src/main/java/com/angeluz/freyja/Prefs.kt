package com.angeluz.freyja

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class SpeakMode { OFF, PUSH_TO_TALK, WAKE_WORD }

object Prefs {
    // Estado del modo de habla
    private val _speakModeFlow = MutableStateFlow(SpeakMode.OFF)
    val speakModeFlow: StateFlow<SpeakMode> = _speakModeFlow
    fun setSpeakMode(mode: SpeakMode) { _speakModeFlow.value = mode }

    // Estado de "desbloqueado"
    private val _unlocked = MutableStateFlow(false)
    val isUnlocked: StateFlow<Boolean> = _unlocked
    fun setUnlocked(v: Boolean) { _unlocked.value = v }
}
