package com.angeluz.freyja

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnInvocar: Button = findViewById(R.id.btnInvocar)

        btnInvocar.setOnClickListener {
            invocarTauriel()
        }
    }

    private fun invocarTauriel() {
        try {
            // Script a ejecutar en Termux
            val scriptPath = "/data/data/com.termux/files/home/invocacion-tauriel.sh"

            val intent = Intent("com.termux.RUN_COMMAND")
            intent.setClassName("com.termux", "com.termux.app.RunCommandService")
            intent.putExtra("com.termux.RUN_COMMAND_PATH", scriptPath)
            intent.putExtra("com.termux.RUN_COMMAND_ARGUMENTS", arrayOf<String>())
            intent.putExtra("com.termux.RUN_COMMAND_WORKDIR", "/data/data/com.termux/files/home")
            intent.putExtra("com.termux.RUN_COMMAND_BACKGROUND", false)

            startService(intent)

            Toast.makeText(this, "Invocando a Tauriel… 🌌", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Toast.makeText(this, "Error al invocar a Tauriel: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}