package com.angeluz.freyja

import android.app.*
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

object NotificationUtils {
    const val CHANNEL_ID = "freyja_core"
    const val CHANNEL_NAME = "La Voz de Freyja"
    const val NOTIF_ID = 1226

    fun ensureChannel(ctx: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ch = NotificationChannel(
                CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW
            ).apply { setShowBadge(false) }
            (ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(ch)
        }
    }

    fun ongoing(ctx: Context, text: String): Notification {
        ensureChannel(ctx)
        return NotificationCompat.Builder(ctx, CHANNEL_ID)
            .setContentTitle("La Voz de Freyja")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.star_on)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()
    }
}