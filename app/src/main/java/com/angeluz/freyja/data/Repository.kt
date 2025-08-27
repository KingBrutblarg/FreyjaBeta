package com.angeluz.freyja.data

class Repository {
    private val api = RetrofitProvider.api

    suspend fun fetchPosts(): List<PostDto> {
        return api.getPosts()
    }
}