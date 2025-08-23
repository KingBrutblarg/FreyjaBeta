package com.angeluz.freyja

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Puente “híbrido” para decidir cómo hablar/escuchar según el modo seleccionado.
 * - Usa Prefs.speakModeFlow para reaccionar a cambios
 * - Expone start()/stop() seguros
 */
class HybridInvoker(
    private val appContext: Context
) {
    private val scope = CoroutineScope(Dispatchers.Default + Job())

    @Volatile
    private var currentMode: SpeakMode = SpeakMode.OFF

    fun start() {
        // Observa el modo y aplica estrategia
        scope.launch {
            Prefs.run { appContext.speakModeFlow }.collect { mode ->
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
            SpeakMode.OFF -> {
                // Nada. Podrías mostrar un toast/log si quieres.
            }
            SpeakMode.PUSH_TO_TALK -> {
                // Aquí arrancarías lógica de “mantener pulsado para hablar”
                // Ejemplo simulado:
                // pushToTalkEngine = PushToTalkEngine(...).also { it.start() }
            }
            SpeakMode.WAKE_WORD -> {
                // Aquí engancharías detector de palabra clave y TTS
                // wakeWordEngine = HotwordEngine(...).also { it.start() }
            }
        }
    }

    private fun stopInternal() {
        // Apaga todo lo que tengas en curso. Este es un no-op ahora mismo.
        // wakeWordEngine?.stop(); pushToTalkEngine?.stop(); tts?.shutdown() ...
    }

    // Atajos de conveniencia para otras capas:
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
