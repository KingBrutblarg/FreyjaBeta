package com.angeluz.freyja
import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale
class TtsManager(private val context: Context) : TextToSpeech.OnInitListener {
  private var tts: TextToSpeech? = null
  init {
    try { tts = TextToSpeech(context, this, "com.github.olga_yakovleva.rhvoice.android") }
    catch (e: Exception) { Log.w("TTS","RHVoice no disponible, usando engine por defecto"); tts = TextToSpeech(context, this) }
  }
  override fun onInit(status: Int) { if (status == TextToSpeech.SUCCESS) { tts?.language = Locale("es","MX"); tts?.setPitch(1.0f); tts?.setSpeechRate(1.0f) } }
  fun speak(text: String) { tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, System.currentTimeMillis().toString()) }
  fun shutdown() { tts?.shutdown() }
}
