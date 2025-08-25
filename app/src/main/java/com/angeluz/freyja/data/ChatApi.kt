package com.angeluz.freyja.data

import retrofit2.http.Body
import retrofit2.http.POST

// 🔸 Modelos sencillos
data class ChatRequest(val message: String)
data class ChatResponse(val reply: String)

// 🔸 Endpoint (ajusta la ruta si tu backend usa otra)
interface ChatApi {
    @POST("chat")
    suspend fun send(@Body body: ChatRequest): ChatResponse
}