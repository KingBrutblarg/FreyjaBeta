package com.angeluz.freyja.data

import retrofit2.http.GET

// Define tu API (ajusta el endpoint según tu backend)
interface ApiService {
    @GET("posts")
    suspend fun getPosts(): List<PostDto>
}

// DTO temporal
data class PostDto(
    val id: Int,
    val title: String,
    val body: String
)