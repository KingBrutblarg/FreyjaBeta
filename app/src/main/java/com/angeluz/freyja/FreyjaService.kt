package com.angeluz.freyja

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*

class FreyjaService : Service() {

    companion object {
        private const val CHANNEL_ID = "freyja_core"
        const val ACT_GEN_IMAGE_LOCAL = "FREYJA_GEN_IMAGE_LOCAL"
        const val ACT_GEN_IMAGE_STABILITY = "FREYJA_GEN_IMAGE_STABILITY"
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        createChannel()

        val notif = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Freyja en guardia")
            .setContentText("Monitoreo activo para emergencias")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
            .build()
        startForeground(1226, notif)

        // Conciencia: percepción + reglas
        Awareness.start(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACT_GEN_IMAGE_LOCAL -> {
                val prompt = intent.getStringExtra("prompt")
                    ?: "runa de protección vikinga, arte rúnico místico"
                val baseUrl = intent.getStringExtra("baseUrl") ?: "http://127.0.0.1:7860"
                scope.launch {
                    val uri = ImageMaker.a1111Txt2Img(this@FreyjaService, baseUrl, prompt)
                    if (uri != null)
                        FreyjaNotifier.alert(this@FreyjaService, "Imagen generada", "Guardada en Galería", 4001)
                    else
                        FreyjaNotifier.alert(this@FreyjaService, "Fallo generación", "Revisa el backend local", 4002)
                }
            }
            ACT_GEN_IMAGE_STABILITY -> {
                val prompt = intent.getStringExtra("prompt")
                    ?: "diosa nórdica freyja, escudo, runas, estilo épico"
                val apiKey = intent.getStringExtra("apiKey") ?: return START_STICKY
                scope.launch {
                    val uri = ImageMaker.stabilityTxt2Img(this@FreyjaService, apiKey, prompt)
                    if (uri != null)
                        FreyjaNotifier.alert(this@FreyjaService, "Imagen generada", "Guardada en Galería", 4003)
                    else
                        FreyjaNotifier.alert(this@FreyjaService, "Fallo generación", "Revisa la API key o red", 4004)
                }
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        Awareness.stop()
        scope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mgr = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val ch = NotificationChannel(
                CHANNEL_ID,
                "Core de supervivencia",
                NotificationManager.IMPORTANCE_LOW
            )
            mgr.createNotificationChannel(ch)
        }
    }
}