package com.angeluz.freyja.data

import com.angeluz.freyja.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitProvider {
    private val okHttp by lazy {
        OkHttpClient.Builder().build()
    }

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL) // termina en "/"
            .client(okHttp)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }
}