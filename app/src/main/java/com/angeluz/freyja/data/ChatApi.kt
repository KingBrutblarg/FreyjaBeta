package com.angeluz.freyja.data

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

// Usa la clase ChatRequest que ya existe en ChatRequest.kt
// y ChatReply que definimos en ChatReply.kt
interface ChatApi {
    @POST("chat")
    suspend fun chat(@Body req: ChatRequest): ChatReply
}

object RetrofitProvider {
    val api: ChatApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://TU_BACKEND/") // TODO: reemplaza por tu endpoint (termina en /)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(ChatApi::class.java)
    }
}
