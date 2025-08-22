package com.angeluz.freyja

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat

class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Arranca el servicio tras reinicio
        ContextCompat.startForegroundService(
            context, Intent(context, FreyjaService::class.java)
        )
    }
}