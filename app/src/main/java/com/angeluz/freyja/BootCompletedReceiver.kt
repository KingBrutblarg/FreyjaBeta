package com.angeluz.freyja
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
class BootCompletedReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
      context.startForegroundService(Intent(context, FreyjaService::class.java))
    }
  }
}
