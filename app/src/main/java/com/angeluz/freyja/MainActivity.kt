package com.angeluz.freyja

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import com.angeluz.freyja.databinding.ActivityMainBinding

// Extensión de Context para invocar Termux
fun android.content.Context.runInTermux(command: String) {
    val intent = Intent("com.termux.RUN_COMMAND").apply {
        // 👇 clave: receiver explícito de Termux
        setClassName("com.termux", "com.termux.app.RunCommandReceiver")
        putExtra("background", true)
        putExtra("cwd", "/data/data/com.termux/files/home") // HOME de Termux
        putExtra("command", """sh -lc "$command" """)
    }
    sendBroadcast(intent)
}

class MainActivity : ComponentActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Arrancar el servicio en 1º plano de forma segura
        ContextCompat.startForegroundService(this, Intent(this, FreyjaService::class.java))

        binding.btnInvoke.setOnClickListener {
            // Probar TTS
            val intent = Intent(FreyjaService.ACTION_SAY).apply {
                putExtra(FreyjaService.EXTRA_TEXT, "La diosa te escucha guerrero mio")
            }
            sendBroadcast(intent)

            // Probar Termux -> crear archivo en $HOME
            runInTermux("""echo FREYJA_OK > "$HOME/freyja_ok_1.txt"""")

            Toast.makeText(this, "Broadcast enviado a Termux", Toast.LENGTH_SHORT).show()
        }

        binding.btnEnableHotword.setOnClickListener {
            val intent = Intent(FreyjaService.ACTION_TOGGLE_HOTWORD)
            sendBroadcast(intent)
        }
    }
}
