package com.angeluz.freyja.data

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.angeluz.freyja.BuildConfig

object RetrofitProvider {
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL) // viene de build.gradle.kts
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}