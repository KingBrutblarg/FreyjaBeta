package com.angeluz.freyja

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.angeluz.freyja.model.ChatMessage
import java.util.concurrent.atomic.AtomicLong

class ChatViewModel : ViewModel() {

    private val _messages = mutableStateListOf<ChatMessage>()
    val messages: List<ChatMessage> get() = _messages

    private val nextId = AtomicLong(1L)

    fun send(text: String) {
        _messages.add(ChatMessage(id = nextId.getAndIncrement(), text = text, mine = true))
        _messages.add(ChatMessage(id = nextId.getAndIncrement(), text = "Echo: $text", mine = false))
    }
}
