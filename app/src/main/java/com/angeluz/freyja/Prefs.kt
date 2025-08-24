package com.angeluz.freyja

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class SpeakMode { OFF, PUSH_TO_TALK, WAKE_WORD }

object Prefs {
    // Ajusta si en tu app real viene de DataStore/Room/etc.
    private val _speakModeFlow = MutableStateFlow(SpeakMode.OFF)
    val speakModeFlow: StateFlow<SpeakMode> = _speakModeFlow
}
