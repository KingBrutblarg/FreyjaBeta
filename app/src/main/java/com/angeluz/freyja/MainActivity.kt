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

        // Inicia tu servicio en primer plano (como ya lo tenías)
        startForegroundService(Intent(this, FreyjaService::class.java))

        // --- BOTÓN INVOCAR VOZ ---
        binding.btnInvoke.setOnClickListener {
            // Intent oficial para ejecutar comandos en Termux
            val run = Intent("com.termux.app.RUN_COMMAND").apply {
                setClassName("com.termux", "com.termux.app.RunCommandService")
                putExtra(
                    "com.termux.RUN_COMMAND_PATH",
                    "/data/data/com.termux/files/usr/bin/bash"
                )
                putExtra(
                    "com.termux.RUN_COMMAND_ARGUMENTS",
                    arrayOf("-lc", "\$HOME/invocar_tauriel.sh")
                )
                // Ejecutar en background (no abre Termux al frente)
                putExtra("com.termux.RUN_COMMAND_BACKGROUND", true)
                // Directorio de trabajo de Termux
                putExtra("com.termux.RUN_COMMAND_WORKDIR", "/data/data/com.termux/files/home")
            }

            try {
                startService(run)
            } catch (e: Exception) {
                e.printStackTrace()
                // Fallback: usa tu servicio para hablar un mensaje
                val intent = Intent(FreyjaService.ACTION_SAY).apply {
                    putExtra(
                        FreyjaService.EXTRA_TEXT,
                        "No pude invocar a Termux. Verifica instalación y permisos."
                    )
                }
                sendBroadcast(intent)
            }
        }

        // --- BOTÓN HOTWORD (tu lógica actual del servicio) ---
        binding.btnEnableHotword.setOnClickListener {
            val intent = Intent(FreyjaService.ACTION_TOGGLE_HOTWORD)
            sendBroadcast(intent)
        }
    }
}

binding.btnInvoke.setOnClickListener {
    val run = Intent("com.termux.app.RUN_COMMAND").apply {
        setClassName("com.termux", "com.termux.app.RunCommandService")
        putExtra("com.termux.RUN_COMMAND_PATH", "/data/data/com.termux/files/usr/bin/bash")
        putExtra("com.termux.RUN_COMMAND_ARGUMENTS", arrayOf("-lc", "termux-toast 'Hola desde Freyja'"))
        putExtra("com.termux.RUN_COMMAND_BACKGROUND", false) // tráete Termux al frente para ver qué pasa
        putExtra("com.termux.RUN_COMMAND_WORKDIR", "/data/data/com.termux/files/home")
    }
    try { startService(run) } catch (e: Exception) { e.printStackTrace() }
}