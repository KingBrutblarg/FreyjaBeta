package com.angeluz.freyja.data

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

// --- Modelos mínimos (ajústalos a tu backend real)
data class ChatRequest(val prompt: String)

data class ChatReply(val text: String)

// --- API REST
interface ChatApi {
    @POST("chat")
    suspend fun chat(@Body req: ChatRequest): ChatReply
}

// --- Retrofit Provider
object RetrofitProvider {
    val api: ChatApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://TU_BACKEND/") // TODO: pon tu endpoint real (termina en /)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(ChatApi::class.java)
    }
}
