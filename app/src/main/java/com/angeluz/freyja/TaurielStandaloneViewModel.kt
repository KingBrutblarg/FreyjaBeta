package com.angeluz.freyja

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class Msg(val role:String, val content:String)

class TaurielStandaloneViewModel: ViewModel() {
    private val _loaded = MutableStateFlow(false); val loaded = _loaded.asStateFlow()
    private val _guard  = MutableStateFlow(false); val guard  = _guard.asStateFlow()
    val messages = mutableListOf<Msg>()
    var lastAssistant: String = ""

    fun unlock(key:String) { if (key == "1226") _guard.value = true }

    fun load(path:String) {
        viewModelScope.launch(Dispatchers.IO) { _loaded.value = LlamaBridge.initModel(path, 4096) }
    }

    fun ask(text:String) {
        if (text.isBlank() || !_loaded.value) return
        messages.add(Msg("user", text))
        viewModelScope.launch(Dispatchers.IO) {
            val prompt = buildString {
                append("Eres Tauriel/Freyja, valquiria hispanohablante, concisa, empática.\n")
                messages.takeLast(8).forEach { append("${it.role}: ${it.content}\n") }
                append("assistant: ")
            }
            val out = LlamaBridge.complete(prompt, 256, 0.7f)
            lastAssistant = out
            messages.add(Msg("assistant", out))
        }
    }
}