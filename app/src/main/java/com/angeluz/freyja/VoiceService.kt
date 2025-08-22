package com.angeluz.freyja

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import kotlinx.coroutines.*

class VoiceService : Service() {

    companion object {
        const val CHANNEL_ID = "freyja_voice_channel"
        private const val CHANNEL_NAME = "Freyja Voice"
        private const val NOTIF_ID = 1226
    }

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        // Fallback: por si App.kt aún no registró el canal (no estorba si ya existe)
        ensureChannel()
        startInForeground()

        scope.launch {
            // TODO: aquí integras RHVoice / hotword / cliente local, etc.
            // initRhvoice()
            // startHotwordLoop()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Maneja acciones (START/STOP/RELOAD) si lo deseas
        return START_STICKY
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startInForeground() {
        val openAppIntent = Intent(this, MainActivity::class.java)
        val pi = PendingIntent.getActivity(
            this, 0, openAppIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            else PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("La Voz de Freyja")
            .setContentText("Vigilando el viento y el susurro de tu invocación…")
            .setSmallIcon(R.drawable.ic_stat_freyja) // usa el vector de abajo
            .setContentIntent(pi)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()

        startForeground(NOTIF_ID, notification)
    }

    private fun ensureChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService<NotificationManager>() ?: return
            val existing = nm.getNotificationChannel(CHANNEL_ID)
            if (existing == null) {
                nm.createNotificationChannel(
                    NotificationChannel(
                        CHANNEL_ID,
                        CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_LOW
                    ).apply { description = "Canal para el servicio de voz de Freyja" }
                )
            }
        }
    }
}