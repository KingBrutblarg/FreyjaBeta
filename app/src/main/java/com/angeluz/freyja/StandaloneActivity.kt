package com.angeluz.freyja

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class StandaloneActivity : AppCompatActivity() {

    private lateinit var txtChat: TextView
    private lateinit var edtPrompt: EditText
    private lateinit var btnSend: Button

    // Elegir carpeta SOLO si aún no está configurada
    private val openTree = registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri: Uri? ->
        if (uri != null) {
            val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            contentResolver.takePersistableUriPermission(uri, flags)
            ModelStorage.saveModelsTreeUri(this, uri)
            autoScanAndLoad() // reintentar de inmediato con la carpeta ya guardada
        } else {
            txtChat.append("\n\n❌ No se eligió carpeta. Sin modelo.")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_standalone)

        txtChat = findViewById(R.id.txtChat)
        edtPrompt = findViewById(R.id.edtPrompt)
        btnSend = findViewById(R.id.btnSend)

        // 1) Intentar cargar el último modelo ya interno (si existe)
        val existing = ModelStorage.latestModelFile(this)
        if (existing != null) {
            val ok = LlamaBridge.initModel(existing.absolutePath)
            txtChat.text = if (ok) "⚡ Modelo cargado: ${existing.name}" else "❌ Error cargando ${existing.name}"
        } else {
            txtChat.text = "⏳ Buscando modelo…"
        }

        // 2) AUTO-SCAN: si no hay modelo interno, busca y mueve el más reciente desde la carpeta configurada
        if (existing == null) autoScanAndLoad()

        // Chat
        btnSend.setOnClickListener {
            val prompt = edtPrompt.text.toString().trim()
            if (prompt.isNotEmpty()) {
                val out = LlamaBridge.infer(prompt)
                txtChat.append("\n\n👤: $prompt\n\n🤖: $out")
                edtPrompt.setText("")
            }
        }
    }

    /** Busca en la carpeta elegida; si no hay carpeta, la pide una vez. */
    private fun autoScanAndLoad() {
        val savedTree = ModelStorage.getModelsTreeUri(this)
        if (savedTree == null) {
            // Primer uso: pedir carpeta
            AlertDialog.Builder(this)
                .setTitle("Configurar carpeta de modelos")
                .setMessage("Elige tu carpeta donde guardas los .gguf (por ejemplo Download o termux_backup/mistral-models). Esto se guarda y no se volverá a pedir.")
                .setPositiveButton("Elegir carpeta") { _, _ -> openTree.launch(null) }
                .setNegativeButton("Cancelar") { _, _ ->
                    txtChat.text = "Sin modelo. Ve a Ajustes y configura la carpeta."
                }
                .show()
            return
        }

        // Con carpeta configurada: buscar el .gguf más reciente
        val uri = ModelStorage.findLatestGgufInTree(this)
        if (uri == null) {
            txtChat.text = "No encontré .gguf en la carpeta configurada."
            return
        }

        // Mover (copiar→verificar→borrar origen si se puede)
        val path = ModelStorage.moveModelFromUri(this, uri)
        if (path != null) {
            val ok = LlamaBridge.initModel(path)
            val name = File(path).name
            txtChat.text = if (ok) "⚡ Modelo movido y cargado: $name"
                           else "❌ Falló inicializar $name (pero quedó movido)."
        } else {
            txtChat.text = "❌ Falló mover el modelo desde la carpeta."
        }
    }
}