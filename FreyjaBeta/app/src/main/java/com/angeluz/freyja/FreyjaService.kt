package com.angeluz.freyja
import android.app.*
import android.content.*
import android.os.*
import androidx.core.app.NotificationCompat
class FreyjaService : Service() {
  companion object {
    const val CHANNEL_ID = "freyja_guardia"
    const val NOTIF_ID = 1226
    const val ACTION_SAY = "com.angeluz.freyja.SAY"
    const val ACTION_TOGGLE_HOTWORD = "com.angeluz.freyja.TOGGLE_HOTWORD"
    const val EXTRA_TEXT = "text"
  }
  private lateinit var tts: TtsManager
  private var hotwordEnabled = false
  private var hotword: HotwordStub? = null
  private val receiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
      when (intent.action) {
        ACTION_SAY -> tts.speak(intent.getStringExtra(EXTRA_TEXT) ?: return)
        ACTION_TOGGLE_HOTWORD -> toggleHotword()
      }
    }
  }
  override fun onCreate() {
    super.onCreate()
    tts = TtsManager(this)
    createChannel()
    startForeground(NOTIF_ID, buildNotification("En guardia"))
    registerReceiver(receiver, IntentFilter().apply {
      addAction(ACTION_SAY); addAction(ACTION_TOGGLE_HOTWORD)
    })
  }
  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int) = START_STICKY
  override fun onDestroy() { super.onDestroy(); unregisterReceiver(receiver); hotword?.stop(); tts.shutdown() }
  override fun onBind(intent: Intent?) = null
  private fun createChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val ch = NotificationChannel(CHANNEL_ID, "Guardia de Freyja", NotificationManager.IMPORTANCE_MIN)
      getSystemService(NotificationManager::class.java).createNotificationChannel(ch)
    }
  }
  private fun buildNotification(status: String): Notification {
    val pi = PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE)
    return NotificationCompat.Builder(this, CHANNEL_ID)
      .setContentTitle("Freyja en guardia").setContentText(status)
      .setSmallIcon(R.drawable.ic_freyja_guardia).setContentIntent(pi).setOngoing(true).build()
  }
  private fun toggleHotword() {
    hotwordEnabled = !hotwordEnabled
    if (hotwordEnabled) { if (hotword==null) hotword = HotwordStub(this){ onHotword() }; hotword?.start(); updateNotif("Escuchando… (experimental)") }
    else { hotword?.stop(); updateNotif("En guardia") }
  }
  private fun updateNotif(text: String) { getSystemService(NotificationManager::class.java).notify(NOTIF_ID, buildNotification(text)) }
  private fun onHotword() { tts.speak("La diosa te escucha guerrero mio") }
}
