package com.angeluz.freyja

import android.app.Service
import android.content.Intent
import android.os.IBinder
import kotlinx.coroutines.*

class VoiceService : Service() {
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        scope.launch {
            // Aquí integrarás RHVoice / tu invocación
            // Ejemplo simulado:
            println("🌌 Freyja Voice Service iniciado en background")
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}