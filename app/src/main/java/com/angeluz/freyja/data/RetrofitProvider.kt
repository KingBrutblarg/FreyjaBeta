package com.angeluz.freyja.data

import com.angeluz.freyja.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Proveedor singleton de Retrofit.
 * Usa BuildConfig.API_BASE_URL (definido en build.gradle.kts).
 */
object RetrofitProvider {

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL) // <<--- Aquí se inyecta tu endpoint
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }
}