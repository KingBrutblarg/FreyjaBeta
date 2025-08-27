package com.angeluz.freyja.data

import retrofit2.http.GET

interface ApiService {
    @GET("posts")
    suspend fun getPosts(): List<PostDto>
}

data class PostDto(
    val userId: Int,
    val id: Int,
    val title: String,
    val body: String
)