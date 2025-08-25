package com.angeluz.freyja

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.angeluz.freyja.data.ChatRequest
import com.angeluz.freyja.data.RetrofitProvider
import kotlinx.coroutines.launch

data class ChatMessage(val text: String, val mine: Boolean)

class ChatViewModel : ViewModel() {

    val messages = mutableStateListOf<ChatMessage>()

    fun send(message: String) {
        val trimmed = message.trim()
        if (trimmed.isEmpty()) return

        messages.add(ChatMessage(trimmed, mine = true))

        viewModelScope.launch {
            runCatching {
                RetrofitProvider.api.send(ChatRequest(trimmed))
            }.onSuccess { resp ->
                messages.add(ChatMessage(resp.reply, mine = false))
            }.onFailure { e ->
                messages.add(ChatMessage("⚠️ ${e.message ?: "Error"}", mine = false))
            }
        }
    }
}
