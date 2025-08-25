package com.angeluz.freyja.data

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface ChatApi {
    @POST("chat")
    suspend fun chat(@Body req: ChatRequest): ChatReply
}

object RetrofitProvider {
    val api: ChatApi by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL) // termina en "/"
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(ChatApi::class.java)
    }
}