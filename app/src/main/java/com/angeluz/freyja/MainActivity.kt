package com.angeluz.freyja

import android.content.ActivityNotFoundException
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

        // Inicia tu servicio en primer plano (si lo usas)
        startForegroundService(Intent(this, FreyjaService::class.java))

        // Botón "INVOCAR VOZ"
        binding.btnInvoke.setOnClickListener {
            invocarTauriel()                // <- llama al script ~/invocacion-tauriel.sh
            // o prueba rápida:
            // invocarComando("termux-tts-speak 'Hola desde Freyja'")
        }

        // Botón "HOTWORD (EXPERIMENTAL)"
        binding.btnEnableHotword.setOnClickListener {
            val intent = Intent(FreyjaService.ACTION_TOGGLE_HOTWORD)
            sendBroadcast(intent)
        }
    }

    /**
     * Lanza en Termux el script ~/invocacion-tauriel.sh dentro de un bash login shell.
     * Requiere en Termux: ~/.termux/termux.properties con `allow-external-apps=true`
     * y luego ejecutar `termux-reload-settings`.
     */
    private fun invocarTauriel() {
        val run = Intent("com.termux.RUN_COMMAND").apply {
            setClassName("com.termux", "com.termux.app.RunCommandService")
            putExtra(
                "com.termux.RUN_COMMAND_PATH",
                "/data/data/com.termux/files/usr/bin/bash"
            )
            putExtra(
                "com.termux.RUN_COMMAND_ARGUMENTS",
                arrayOf("-lc", "~/invocacion-tauriel.sh")
            )
            putExtra(
                "com.termux.RUN_COMMAND_WORKDIR",
                "/data/data/com.termux/files/home"
            )
            // true = en segundo plano (sin traer Termux al frente)
            putExtra("com.termux.RUN_COMMAND_BACKGROUND", true)
        }

        try {
            startService(run)
            Toast.makeText(this, "Invocando a Tauriel en Termux…", Toast.LENGTH_SHORT).show()
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                this,
                "No encuentro Termux. ¿Está instalado?",
                Toast.LENGTH_LONG
            ).show()
        } catch (e: SecurityException) {
            Toast.makeText(
                this,
                "Permiso denegado por Termux. Activa allow-external-apps y reinicia Termux.",
                Toast.LENGTH_LONG
            ).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Utilidad: ejecutar un comando suelto (por ejemplo, para pruebas rápidas).
     * Ejemplo: invocarComando("termux-tts-speak 'Hola desde Freyja'")
     */
    private fun invocarComando(comando: String) {
        val run = Intent("com.termux.RUN_COMMAND").apply {
            setClassName("com.termux", "com.termux.app.RunCommandService")
            putExtra(
                "com.termux.RUN_COMMAND_PATH",
                "/data/data/com.termux/files/usr/bin/bash"
            )
            putExtra(
                "com.termux.RUN_COMMAND_ARGUMENTS",
                arrayOf("-lc", comando)
            )
            putExtra(
                "com.termux.RUN_COMMAND_WORKDIR",
                "/data/data/com.termux/files/home"
            )
            putExtra("com.termux.RUN_COMMAND_BACKGROUND", false) // visible para depurar
        }

        try {
            startService(run)
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
