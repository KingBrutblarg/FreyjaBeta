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

    fun send(message: String) {
        viewModelScope.launch {
            _error.value = null
            _loading.value = true
            try {
                val res = RetrofitProvider.sendChat(ChatRequest(message))
                _reply.value = res
            } catch (t: Throwable) {
                _error.value = t.message ?: "Error desconocido"
            } finally {
                _loading.value = false
            }
        }
    }
}
