package com.angeluz.freyja

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.angeluz.freyja.databinding.ActivityMainBinding

class MainActivity : ComponentActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var tts: TtsManager

    private enum class InvocMode { TERMUX, NATIVO, REMOTO }

    private val PREFS = "freyja_prefs"
    private val KEY_MODE = "invocation_mode"

    private var modoActual: InvocMode = InvocMode.TERMUX

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Servicio en primer plano (hotword, etc.)
        startForegroundService(Intent(this, FreyjaService::class.java))

        // TTS nativo
        tts = TtsManager(this)

        // Cargar modo guardado
        modoActual = cargarModo()

        // --- Botón INVOCAR VOZ ---
        binding.btnInvoke.setOnClickListener {
            when (modoActual) {
                InvocMode.TERMUX -> invocarTauriel() // ejecuta ~/invocacion-tauriel.sh en Termux
                InvocMode.NATIVO -> tts.speak("Invocación recibida")
                InvocMode.REMOTO -> invocarRemoto("Invocación recibida")
            }
        }

        // Pulsación larga: rotar modo (TERMUX -> NATIVO -> REMOTO -> …)
        binding.btnInvoke.setOnLongClickListener {
            modoActual = when (modoActual) {
                InvocMode.TERMUX -> InvocMode.NATIVO
                InvocMode.NATIVO -> InvocMode.REMOTO
                InvocMode.REMOTO -> InvocMode.TERMUX
            }
            guardarModo(modoActual)
            Toast.makeText(this, "Modo: ${modoActual.name}", Toast.LENGTH_SHORT).show()
            true
        }

        // --- Botón HOTWORD (EXPERIMENTAL) ---
        binding.btnEnableHotword.setOnClickListener {
            val intent = Intent(FreyjaService.ACTION_TOGGLE_HOTWORD)
            sendBroadcast(intent)
        }
    }

    // --------- Invocación TERMUX ----------
    // Requiere en Termux:
    // 1) ~/.termux/termux.properties con allow-external-apps=true  (y termux-reload-settings)
    // 2) Script ~/invocacion-tauriel.sh con 'termux-toast' / 'termux-tts-speak' (ya lo tienes)
    private fun invocarTauriel() {
        val run = Intent("com.termux.RUN_COMMAND").apply {
            setClassName("com.termux", "com.termux.app.RunCommandService")
            putExtra("com.termux.RUN_COMMAND_PATH",
                "/data/data/com.termux/files/usr/bin/bash")
            putExtra("com.termux.RUN_COMMAND_ARGUMENTS",
                arrayOf("-lc", "~/invocacion-tauriel.sh"))
            putExtra("com.termux.RUN_COMMAND_WORKDIR",
                "/data/data/com.termux/files/home")
            putExtra("com.termux.RUN_COMMAND_BACKGROUND", true)
        }
        try {
            startService(run)
            Toast.makeText(this, "Invocando en Termux…", Toast.LENGTH_SHORT).show()
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "No encuentro Termux. ¿Está instalado?", Toast.LENGTH_LONG).show()
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

    // --------- Invocación REMOTA ----------
    // Placeholder: aquí harás la llamada a tu backend (WebSocket/HTTP).
    // Por ahora mandamos un broadcast interno al servicio para que tengas el “end-to-end”.
    private fun invocarRemoto(frase: String) {
        try {
            val intent = Intent(this, FreyjaService::class.java).apply {
                action = "FREYJA_REMOTE_INVOCATION"
                putExtra("phrase", frase)
            }
            startService(intent)
            Toast.makeText(this, "Invocación remota enviada", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Remoto error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    // --------- Persistencia del modo ----------
    private fun guardarModo(modo: InvocMode) {
        getSharedPreferences(PREFS, MODE_PRIVATE)
            .edit()
            .putString(KEY_MODE, modo.name)
            .apply()
    }

    private fun cargarModo(): InvocMode {
        val name = getSharedPreferences(PREFS, MODE_PRIVATE)
            .getString(KEY_MODE, InvocMode.TERMUX.name)
        return runCatching { InvocMode.valueOf(name!!) }.getOrDefault(InvocMode.TERMUX)
    }

    override fun onDestroy() {
        super.onDestroy()
        tts.shutdown()
    }
}