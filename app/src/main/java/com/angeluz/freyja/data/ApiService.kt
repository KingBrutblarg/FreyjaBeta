package com.angeluz.freyja.data

import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("/")
    suspend fun ping(): Response<Unit>
}
