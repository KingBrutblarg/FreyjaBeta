package com.angeluz.freyja

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class FreyjaService : Service() {

    companion object {
        const val CHANNEL_ID = "freyja_voice_channel"
        private const val NOTIF_ID = 1226
    }

    private var invoker: HybridInvoker? = null

    override fun onCreate() {
        super.onCreate()
        startInForeground()
        invoker = HybridInvoker(applicationContext).also { it.start() }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        invoker?.stop()
        invoker = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startInForeground() {
        val openAppIntent = Intent(this, MainActivity::class.java)
        val pi = PendingIntent.getActivity(
            this, 0, openAppIntent,
            if (Build.VERSION.SDK_INT >= 31)
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            else PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("La Voz de Freyja")
            .setContentText("Vigilando el susurro de tu invocación…")
            .setSmallIcon(R.drawable.ic_stat_freyja)
            .setContentIntent(pi)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()

        startForeground(NOTIF_ID, notification)
    }
}
