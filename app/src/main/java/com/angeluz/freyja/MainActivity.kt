package com.angeluz.freyja
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.angeluz.freyja.databinding.ActivityMainBinding
class MainActivity : ComponentActivity() {
  private lateinit var binding: ActivityMainBinding
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)
    startForegroundService(Intent(this, FreyjaService::class.java))
    binding.btnInvoke.setOnClickListener {
      val intent = Intent(FreyjaService.ACTION_SAY).apply {
        putExtra(FreyjaService.EXTRA_TEXT, "La diosa te escucha guerrero mio")
      }
      sendBroadcast(intent)
    }
    binding.btnEnableHotword.setOnClickListener {
      val intent = Intent(FreyjaService.ACTION_TOGGLE_HOTWORD)
      sendBroadcast(intent)
    }
  }
}
