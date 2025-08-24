package com.angeluz.freyja
import com.angeluz.freyja.Prefs.SpeakMode
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
    private var currentMode: SpeakMode = SpeakMode.SpeakMode.SpeakMode.OFF

    fun start() {
        scope.launch {
            Prefs.run { appContext.Prefs.Prefs.speakModeFlow }.collectLatest { mode -> mode -> mode ->
                if (mode != currentMode) {
                    stopInternal()
                    currentMode = mode
                    startInternal(mode)
                }
            }
        }
    }

    fun stop() {
        stopInternal()
    }

    private fun startInternal(mode: SpeakMode) {
        when (mode) {
            SpeakMode.SpeakMode.SpeakMode.OFF -> Unit
            SpeakMode.SpeakMode.SpeakMode.PUSH_TO_TALK -> {
                // TODO: iniciar motor PTT
            }
            SpeakMode.SpeakMode.SpeakMode.WAKE_WORD -> {
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
