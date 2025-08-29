package com.angeluz.freyja

import android.net.Uri
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

class StandaloneActivity : ComponentActivity() {
    private var tts: TextToSpeech? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(this) { if (it == TextToSpeech.SUCCESS) tts?.language = Locale("es","MX") }

        setContent {
            MaterialTheme(colorScheme = darkColorScheme(
                primary = Color(0xFFBFA6FF),
                background = Color(0xFF0E0D12),
                surface = Color(0xFF14121A)
            )) {
                val vm: TaurielStandaloneViewModel = viewModel()
                val scope = rememberCoroutineScope()

                val pick = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
                    if (uri != null) scope.launch(Dispatchers.IO) {
                        val path = copyToFiles(uri, "model.gguf")
                        vm.load(path)
                    }
                }

                Surface(Modifier.fillMaxSize().background(Color(0xFF0E0D12))) {
                    Column(Modifier.fillMaxSize().padding(12.dp)) {
                        Text("ᚠ La Voz de Freyja — Standalone ᚠ", color=Color(0xFFBFA6FF))
                        Spacer(Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Button(onClick = { pick.launch(arrayOf("*/*")) }) { Text("Cargar modelo GGUF") }
                            Spacer(Modifier.width(8.dp))
                            val ready = vm.loaded.collectAsState().value
                            Text(if (ready) "Modelo listo" else "Sin modelo", color=Color.White)
                        }

                        Spacer(Modifier.height(8.dp))
                        LazyColumn(Modifier.weight(1f)) {
                            items(vm.messages) { m ->
                                Column(Modifier.fillMaxWidth().padding(6.dp)
                                    .background(if (m.role=="user") Color(0xFF1D1A25) else Color(0xFF10131A))
                                    .padding(10.dp)) {
                                    Text(if (m.role=="user") "Tú" else "Tauriel", color=Color(0xFFBFA6FF))
                                    Spacer(Modifier.height(4.dp))
                                    Text(m.content, color=Color.White)
                                }
                            }
                        }

                        var input by remember { mutableStateOf("") }
                        var key by remember { mutableStateOf("") }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(value=input, onValueChange={input=it},
                                modifier=Modifier.weight(1f), placeholder={ Text("Háblame…") })
                            Spacer(Modifier.width(8.dp))
                            Button(onClick = {
                                scope.launch { vm.ask(input); tts?.speak(vm.lastAssistant, TextToSpeech.QUEUE_FLUSH, null, "tauriel"); input="" }
                            }, enabled = vm.loaded.collectAsState().value) { Text("Enviar") }
                        }
                        Spacer(Modifier.height(6.dp))
                        OutlinedTextField(value=key, onValueChange={ key=it; vm.unlock(key) },
                            placeholder={ Text("Llave rúnica") }, modifier=Modifier.fillMaxWidth())
                        if (vm.guard.collectAsState().value) {
                            Text("⚔ Guardia de Freyja ACTIVADA", color=Color(0xFFBFA6FF))
                        }
                    }
                }
            }
        }
    }

    private fun copyToFiles(uri: Uri, name: String): String {
        val dest = File(filesDir, "models").apply { mkdirs() }
        val out  = File(dest, name)
        contentResolver.openInputStream(uri)?.use { inp -> out.outputStream().use { inp.copyTo(it) } }
        return out.absolutePath
    }

    override fun onDestroy() { tts?.shutdown(); super.onDestroy() }
}