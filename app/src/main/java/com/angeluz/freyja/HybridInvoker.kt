package com.angeluz.freyja
import com.angeluz.freyja.Prefs

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HybridInvoker(private val appContext: Context) {
    private val scope = CoroutineScope(Dispatchers.Default + Job())

    @Volatile
    private var currentMode: SpeakMode = SpeakMode.OFF

    fun start() {
        scope.launch {
            Prefs.speakModeFlow.collectLatest { mode ->
                if (mode == currentMode) return
                stopInternal()
                currentMode = mode
                when (mode) {
                    com.angeluz.freyja.SpeakMode.OFF -> Unit
                    com.angeluz.freyja.SpeakMode.PUSH_TO_TALK -> { }
                    com.angeluz.freyja.SpeakMode.WAKE_WORD -> { }
                }
            }
        }
    }
        }
    }

    fun stop() {
        stopInternal()
    }

    private fun startInternal(mode: SpeakMode) {
        when (mode) {
            SpeakMode.OFF -> Unit
            SpeakMode.PUSH_TO_TALK -> {
                // TODO: iniciar motor PTT
            }
            SpeakMode.WAKE_WORD -> {
                // TODO: iniciar hotword
            }
        }
    }

    private fun stopInternal() {
        // TODO: apagar motores si existen
    }

    fun setMode(mode: SpeakMode) {
        scope.launch { Prefs.setSpeakMode(appContext, mode) }
    }

    fun unlockVault() {
        scope.launch { Prefs.setUnlocked(appContext, true) }
    }

    fun lockVault() {
        scope.launch { Prefs.setUnlocked(appContext, false) }
    }
}
