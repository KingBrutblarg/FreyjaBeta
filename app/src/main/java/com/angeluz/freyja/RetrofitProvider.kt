package com.angeluz.freyja.data

import com.angeluz.freyja.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitProvider {
    val retrofit: Retrofit by lazy {
        val base = BuildConfig.API_BASE_URL
        require(base.isNotBlank()) { "BuildConfig.API_BASE_URL is blank" }

        Retrofit.Builder()
            .baseUrl(base)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }
}