package com.angeluz.freyja

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import com.angeluz.freyja.databinding.ActivityMainBinding

/**
 * Envía un broadcast a Termux:API para ejecutar un comando en segundo plano.
 * - Usa RunCommandReceiver explícito.
 * - Fuerza cwd al HOME de Termux.
 * - Ejecuta con sh -lc.
 */
fun Context.runInTermux(command: String) {
    val intent = Intent("com.termux.RUN_COMMAND").apply {
        setClassName("com.termux", "com.termux.app.RunCommandReceiver")
        putExtra("background", true)
        putExtra("cwd", "/data/data/com.termux/files/home")
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

        // Servicio en primer plano de forma segura
        ContextCompat.startForegroundService(
            this,
            Intent(this, FreyjaService::class.java)
        )

        // Botón: TTS + prueba Termux
        binding.btnInvoke.setOnClickListener {
            val say = Intent(FreyjaService.ACTION_SAY).apply {
                putExtra(FreyjaService.EXTRA_TEXT, "La diosa te escucha, guerrero mío")
            }
            sendBroadcast(say)

            // SIN $HOME: usamos ruta relativa porque cwd ya es el HOME de Termux
            runInTermux("""echo FREYJA_OK >> freyja_ok_1.txt""")

            Toast.makeText(this, "Broadcast enviado a Termux", Toast.LENGTH_SHORT).show()
        }

        // Botón: activar/desactivar hotword
        binding.btnEnableHotword.setOnClickListener {
            val toggle = Intent(FreyjaService.ACTION_TOGGLE_HOTWORD)
            sendBroadcast(toggle)
        }
    }
}
