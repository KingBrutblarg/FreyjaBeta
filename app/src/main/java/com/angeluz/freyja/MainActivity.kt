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

        // Servicio en primer plano
        startForegroundService(Intent(this, FreyjaService::class.java))

        // --- BOTÓN INVOCAR VOZ (PRUEBA TOAST) ---
        binding.btnInvoke.setOnClickListener {
            val run = Intent("com.termux.app.RUN_COMMAND").apply {
                setClassName("com.termux", "com.termux.app.RunCommandService")
                putExtra("com.termux.RUN_COMMAND_PATH",
                    "/data/data/com.termux/files/usr/bin/bash")
                putExtra("com.termux.RUN_COMMAND_ARGUMENTS",
                    arrayOf("-lc", "termux-toast 'Hola desde Freyja'"))
                // TEMPORAL: trae Termux al frente para ver qué pasa
                putExtra("com.termux.RUN_COMMAND_BACKGROUND", false)
                putExtra("com.termux.RUN_COMMAND_WORKDIR",
                    "/data/data/com.termux/files/home")
            }
            try { startService(run) } catch (e: Exception) { e.printStackTrace() }
        }

        // --- BOTÓN HOTWORD (tu servicio actual) ---
        binding.btnEnableHotword.setOnClickListener {
            val intent = Intent(FreyjaService.ACTION_TOGGLE_HOTWORD)
            sendBroadcast(intent)
        }
    }
}