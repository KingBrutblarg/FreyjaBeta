package com.angeluz.freyja

import android.content.Context
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest

class HybridInvoker(private val appContext: Context) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var currentMode: SpeakMode = SpeakMode.OFF

    fun start() {
        scope.launch {
            Prefs.speakModeFlow.collectLatest { mode ->
                if (mode == currentMode) return@collectLatest
                stopInternal()
                currentMode = mode
                when (mode) {
                    SpeakMode.OFF -> Unit
                    SpeakMode.PUSH_TO_TALK -> {
                        // TODO iniciar PTT
                    }
                    SpeakMode.WAKE_WORD -> {
                        // TODO iniciar hotword
                    }
                }
            }
        }
    }

    fun stop() {
        stopInternal()
    }

    private fun stopInternal() {
        // TODO parar lo que se haya lanzado según currentMode
    }
}
