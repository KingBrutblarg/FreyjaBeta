package com.angeluz.freyja.data

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

// Modelos existentes en tu proyecto:
// - ChatRequest.kt  (ya lo tienes)
// - ChatReply.kt    (si no existe, crea el de abajo)

interface ChatApi {
    @POST("chat")
    suspend fun chat(@Body req: ChatRequest): ChatReply
}

object RetrofitProvider {
    val api: ChatApi by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL) // ← sale de build.gradle.kts
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(ChatApi::class.java)
    }
}