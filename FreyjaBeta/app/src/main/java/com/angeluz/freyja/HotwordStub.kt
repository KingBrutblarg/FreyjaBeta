package com.angeluz.freyja
import android.content.Context
import android.util.Log
import kotlin.concurrent.thread
class HotwordStub(private val ctx: Context, private val onDetect: () -> Unit) {
  @Volatile private var running = false
  fun start() { running = true; thread(name="HotwordStub"){ Log.i("Hotword","Iniciado (stub)"); while(running){ Thread.sleep(8000); onDetect() } } }
  fun stop() { running = false }
}
