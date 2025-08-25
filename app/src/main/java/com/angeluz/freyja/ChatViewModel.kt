package com.angeluz.freyja

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.angeluz.freyja.data.ChatRequest
import com.angeluz.freyja.data.RetrofitProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    private val _reply = MutableStateFlow("")
    val reply: StateFlow<String> = _reply

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun send(prompt: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val r = RetrofitProvider.api.chat(ChatRequest(prompt))
                _reply.value = r.text
            } catch (e: Exception) {
                _error.value = e.message ?: "Error de red"
            } finally {
                _loading.value = false
            }
        }
    }
}
