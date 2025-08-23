package com.angeluz.freyja

import android.content.Context
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

/**
 * Awareness: utilidades rápidas que consultan estado guardado en Prefs.
 */
object Awareness {

    /**
     * ¿Está “desbloqueada” la bóveda?  Bloqueante para simplificar (llámalo fuera del hilo principal).
     */
    fun isUnlockedBlocking(context: Context): Boolean = runBlocking {
        Prefs.run { context.isUnlockedFlow.first() }
    }

    /**
     * Devuelve el modo actual de voz.
     */
    fun currentSpeakModeBlocking(context: Context): SpeakMode = runBlocking {
        Prefs.run { context.speakModeFlow.first() }
    }
}
