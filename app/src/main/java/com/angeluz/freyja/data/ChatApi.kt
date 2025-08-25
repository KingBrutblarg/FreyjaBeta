package com.angeluz.freyja.data

import retrofit2.http.Body
import retrofit2.http.POST

// Modelos simples (ajusta a tu backend si es necesario)
data class ChatRequest(val message: String)
data class ChatResponse(val reply: String)

interface ChatApi {
    @POST("chat")
    suspend fun send(@Body body: ChatRequest): ChatResponse
}
