package com.angeluz.freyja

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.angeluz.freyja.data.ChatApi
import com.angeluz.freyja.data.ChatRequest
import com.angeluz.freyja.data.ChatReply
import com.angeluz.freyja.data.RetrofitProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

<<<<<<< HEAD
class ChatViewModel : ViewModel() {
=======
class ChatViewModel(
    private val api: ChatApi = RetrofitProvider.api
) : ViewModel() {
>>>>>>> fb03f9f (feat(chat): UI ChatScreen + ajustes en ChatViewModel)

    private val _reply = MutableStateFlow<String?>(null)
    val reply: StateFlow<String?> = _reply

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

<<<<<<< HEAD
    fun send(prompt: String) {
        if (prompt.isBlank()) return
        _loading.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                val res: ChatReply = RetrofitProvider.api.chat(ChatRequest(prompt))
                _reply.value = res.text
            } catch (t: Throwable) {
                _error.value = t.message ?: "Error desconocido"
=======
    fun ask(text: String) {
        _error.value = null
        if (text.isBlank()) return
        viewModelScope.launch {
            _loading.value = true
            try {
                // ✅ usa el backend si ya pusiste un baseUrl real,
                //    si no tienes backend aún: descomenta la línea mock y comenta la real.
                val res: ChatReply = api.chat(ChatRequest(text))
                // val res = ChatReply(text) // <-- Mock local (eco normal)
                _reply.value = res.text
            } catch (t: Throwable) {
                _error.value = t.message ?: "Error de red"
>>>>>>> fb03f9f (feat(chat): UI ChatScreen + ajustes en ChatViewModel)
            } finally {
                _loading.value = false
            }
        }
    }
}