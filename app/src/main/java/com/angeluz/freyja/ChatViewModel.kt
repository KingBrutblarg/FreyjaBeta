package com.angeluz.freyja

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.angeluz.freyja.data.ChatRequest
import com.angeluz.freyja.data.ChatReply
import com.angeluz.freyja.data.RetrofitProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val _input = MutableStateFlow("")
    val input: StateFlow<String> = _input

    private val _reply = MutableStateFlow<String?>(null)
    val reply: StateFlow<String?> = _reply

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun onInputChange(text: String) {
        _input.value = text
    }

    fun send() {
        val prompt = input.value.trim()
        if (prompt.isEmpty() || _loading.value) return

        _error.value = null
        _loading.value = true

        viewModelScope.launch {
            try {
                val res: ChatReply = RetrofitProvider.api.chat(ChatRequest(prompt))
                _reply.value = res.text
            } catch (t: Throwable) {
                _error.value = t.message ?: "Error de red"
            } finally {
                _loading.value = false
            }
        }
    }
}