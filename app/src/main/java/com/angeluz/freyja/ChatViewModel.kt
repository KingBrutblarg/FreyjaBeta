package com.angeluz.freyja

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.angeluz.freyja.model.ChatMessage
import java.util.concurrent.atomic.AtomicLong

class ChatViewModel : ViewModel() {

    private val _messages = mutableStateListOf<ChatMessage>()
    val messages: List<ChatMessage> get() = _messages

    private val nextId = AtomicLong(1)

    fun send(text: String) {
        // tu mensaje
        _messages.add(ChatMessage(nextId.getAndIncrement(), text, true))
        // respuesta dummy (quítala si no la quieres)
        _messages.add(ChatMessage(nextId.getAndIncrement(), "Echo: $text", false))
    }
}