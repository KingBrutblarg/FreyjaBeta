package com.angeluz.freyja

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.angeluz.freyja.databinding.ActivityMainBinding

class MainActivity : ComponentActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Arranca tu servicio en primer plano (como ya hacías)
        startForegroundService(Intent(this, FreyjaService::class.java))

        // --- BOTÓN INVOCAR VOZ ---
        binding.btnInvoke.setOnClickListener {
            // 1) Prueba local para confirmar que el click funciona
            Toast.makeText(this, "Invocando…", Toast.LENGTH_SHORT).show()

            // 2) Llamada a Termux
            val run = Intent("com.termux.RUN_COMMAND").apply {
                setClassName("com.termux", "com.termux.app.RunCommandService")
                putExtra(
                    "com.termux.RUN_COMMAND_PATH",
                    "/data/data/com.termux/files/usr/bin/bash"
                )
                putExtra(
                    "com.termux.RUN_COMMAND_ARGUMENTS",
                    arrayOf("-lc", "termux-tts-speak 'Tauriel está lista. ¿En qué te ayudo?'")
                )
                putExtra("com.termux.RUN_COMMAND_BACKGROUND", true) // 2º plano
                putExtra(
                    "com.termux.RUN_COMMAND_WORKDIR",
                    "/data/data/com.termux/files/home"
                )
            }

            try {
                startService(run)
                // Confirma que sí llegamos aquí
                Toast.makeText(this, "Comando enviado a Termux", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }

        // --- BOTÓN HOTWORD ---
        binding.btnEnableHotword.setOnClickListener {
            val intent = Intent(FreyjaService.ACTION_TOGGLE_HOTWORD)
            sendBroadcast(intent)
        }
    }
}