package com.angeluz.freyja.data

import retrofit2.http.GET
import retrofit2.http.Query

interface ChatApi {
    @GET("echo")
    suspend fun echo(@Query("q") q: String): String

    companion object {
        val instance: ChatApi by lazy {
            RetrofitProvider.retrofit.create(ChatApi::class.java)
        }
    }
}