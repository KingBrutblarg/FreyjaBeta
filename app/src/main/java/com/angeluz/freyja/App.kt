// app/src/main/java/com/angeluz/freyja/App.kt
package com.angeluz.freyja

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        createVoiceChannel()
    }

    private fun createVoiceChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = VoiceService.CHANNEL_ID
            val channel = NotificationChannel(
                channelId,
                "Freyja Voice",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Canal para el servicio de voz de Freyja"
                setShowBadge(false)
            }
            val nm = getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(channel)
        }
    }
}