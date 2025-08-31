package com.angeluz.freyja

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import java.io.File

class StandaloneActivity : ComponentActivity() {

    private lateinit var txtChat: TextView
    private lateinit var edtPrompt: EditText
    private lateinit var btnSend: Button

    private val openTree = registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri: Uri? ->
        if (uri != null) {
            val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            contentResolver.takePersistableUriPermission(uri, flags)
            ModelStorage.saveModelsTreeUri(this, uri)
            autoScanAndLoad()
        } else {
            toast("No se eligió carpeta.")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ---- UI programática (sin XML) ----
        val root = ScrollView(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }
        root.addView(content)

        val title = TextView(this).apply {
            text = "Freyja Standalone"
            textSize = 20f
        }
        val btnPickFolder = Button(this).apply { text = "Elegir carpeta de modelos" }
        val btnRescan = Button(this).apply { text = "Re-scan y cargar" }

        txtChat = TextView(this).apply {
            text = "Inicializando…"
            setPadding(0, 16, 0, 16)
        }
        edtPrompt = EditText(this).apply { hint = "Escribe tu mensaje…" }
        btnSend = Button(this).apply { text = "Enviar" }

        content.addView(title)
        content.addView(btnPickFolder)
        content.addView(btnRescan)
        content.addView(txtChat)
        content.addView(edtPrompt)
        content.addView(btnSend)

        setContentView(root)
        // -----------------------------------

        // 1) Intentar cargar modelo interno
        val existing = ModelStorage.latestModelFile(this)
        if (existing != null) {
            val ok = LlamaBridge.initModel(existing.absolutePath)
            txtChat.text = if (ok) "⚡ Modelo cargado: ${existing.name}" else "❌ Error cargando ${existing.name}"
        } else {
            txtChat.text = "⏳ Buscando modelo…"
        }

        // 2) Si no hay, auto-scan (si hay carpeta guardada)
        if (existing == null) autoScanAndLoad()

        btnPickFolder.setOnClickListener { openTree.launch(null) }
        btnRescan.setOnClickListener { autoScanAndLoad(forceAskIfMissing = true) }

        btnSend.setOnClickListener {
            val prompt = edtPrompt.text.toString().trim()
            if (prompt.isNotEmpty()) {
                val out = LlamaBridge.infer(prompt)
                txtChat.append("\n\n👤: $prompt\n\n🤖: $out")
                edtPrompt.setText("")
            }
        }
    }

    private fun autoScanAndLoad(forceAskIfMissing: Boolean = false) {
        val savedTree = ModelStorage.getModelsTreeUri(this)
        if (savedTree == null) {
            if (forceAskIfMissing) {
                AlertDialog.Builder(this)
                    .setTitle("Configurar carpeta de modelos")
                    .setMessage("Elige tu carpeta donde guardas los .gguf (Download o termux_backup/mistral-models).")
                    .setPositiveButton("Elegir") { _, _ -> openTree.launch(null) }
                    .setNegativeButton("Cancelar", null)
                    .show()
            } else {
                txtChat.text = "Sin carpeta de modelos configurada. Usa 'Elegir carpeta de modelos'."
            }
            return
        }

        val uri = ModelStorage.findLatestGgufInTree(this)
        if (uri == null) {
            txtChat.text = "No encontré .gguf en la carpeta configurada."
            return
        }

        val path = ModelStorage.moveModelFromUri(this, uri)
        if (path != null) {
            val ok = LlamaBridge.initModel(path)
            val name = File(path).name
            txtChat.text = if (ok) "⚡ Modelo movido y cargado: $name"
                           else "❌ Falló inicializar $name (quedó movido)."
        } else {
            txtChat.text = "❌ Falló mover el modelo desde la carpeta."
        }
    }

    private fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
