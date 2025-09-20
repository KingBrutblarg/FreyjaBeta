package com.angeluz.freyja

import android.app.Application
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.provider.OpenableColumns
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.angeluz.freyja.ai.FileAnalyzer
import com.angeluz.freyja.model.ChatMessage
import java.util.Locale
import java.util.concurrent.atomic.AtomicLong
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val contentResolver = application.contentResolver
    private val analyzer = FileAnalyzer()
    private val nextId = AtomicLong(1L)

    private val _messages = mutableStateListOf<ChatMessage>()
    val messages: List<ChatMessage> get() = _messages

    init {
        addAssistantMessage(
            "Freyja lista al combate: dime qué sientes o comparte un archivo y lo descifraremos juntas."
        )
    }

    fun send(text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return
        _messages.add(ChatMessage(id = nextId.getAndIncrement(), text = trimmed, mine = true))
        addAssistantMessage("Te escucho. Si necesitas claridad, podemos revisar un documento o seguir charlando.")
    }

    fun processFile(uri: Uri) {
        viewModelScope.launch {
            val displayName = resolveDisplayName(uri)
            val status = ChatMessage(
                id = nextId.getAndIncrement(),
                text = "Examinando $displayName…",
                mine = false
            )
            _messages.add(status)
            val statusIndex = _messages.lastIndex
            val mimeType = contentResolver.getType(uri)
            runCatching {
                analyzeFile(uri, displayName, mimeType)
            }.onSuccess { analysis ->
                _messages[statusIndex] = status.copy(text = "Esto es lo que hallé en $displayName:")
                addAssistantMessage(analysis)
            }.onFailure { throwable ->
                val readable = throwable.localizedMessage?.takeIf { it.isNotBlank() }
                    ?: "No pude interpretar el archivo."
                _messages[statusIndex] = status.copy(text = "Fallé al leer $displayName: $readable")
            }
        }
    }

    private suspend fun analyzeFile(
        uri: Uri,
        displayName: String,
        mimeType: String?
    ): String = withContext(Dispatchers.IO) {
        val lowerMime = mimeType?.lowercase(Locale.ROOT)
        val lowerName = displayName.lowercase(Locale.ROOT)

        return@withContext if (lowerMime?.contains("pdf") == true || lowerName.endsWith(".pdf")) {
            val extracted = readTextFromUri(uri)
            if (!extracted.isNullOrBlank()) {
                analyzer.summarizeText(extracted)
            } else {
                val pageCount = countPdfPages(uri)
                analyzer.describePdf(pageCount)
            }
        } else {
            val text = readTextFromUri(uri)?.trim()
                ?: throw IllegalArgumentException("El archivo no contenía texto legible.")
            if (text.isEmpty()) {
                throw IllegalArgumentException("El archivo no contenía texto legible.")
            }
            analyzer.summarizeText(text)
        }
    }

    private fun readTextFromUri(uri: Uri): String? {
        return contentResolver.openInputStream(uri)?.bufferedReader()?.use { reader ->
            val builder = StringBuilder()
            val buffer = CharArray(BUFFER_SIZE)
            var total = 0
            while (true) {
                val read = reader.read(buffer)
                if (read == -1) break
                val remaining = MAX_TEXT_CHARS - total
                if (remaining <= 0) break
                val safeRead = minOf(read, remaining)
                builder.append(buffer, 0, safeRead)
                total += safeRead
                if (total >= MAX_TEXT_CHARS) {
                    builder.append('\n').append('…')
                    break
                }
            }
            builder.toString()
        }
    }

    private fun countPdfPages(uri: Uri): Int? {
        return contentResolver.openFileDescriptor(uri, "r")?.use { descriptor ->
            PdfRenderer(descriptor).use { renderer -> renderer.pageCount }
        }
    }

    private fun resolveDisplayName(uri: Uri): String {
        val projection = arrayOf(OpenableColumns.DISPLAY_NAME)
        return contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1 && cursor.moveToFirst()) {
                cursor.getString(nameIndex)
            } else {
                null
            }
        } ?: uri.lastPathSegment ?: "archivo"
    }

    private fun addAssistantMessage(text: String) {
        _messages.add(ChatMessage(id = nextId.getAndIncrement(), text = text, mine = false))
    }

    companion object {
        private const val BUFFER_SIZE = 4_096
        private const val MAX_TEXT_CHARS = 40_000
    }
}
