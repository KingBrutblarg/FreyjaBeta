package com.angeluz.freyja.data

object RetrofitProvider {
    suspend fun sendChat(req: ChatRequest): String {
        // Simula una respuesta remota
        return "Freyja dice: ${req.message.reversed()}"
    }
}
