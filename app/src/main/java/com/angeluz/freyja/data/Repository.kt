package com.angeluz.freyja.data

import kotlinx.coroutines.delay

class Repository(
    private val api: ApiService = RetrofitProvider.retrofit.create(ApiService::class.java)
) {
    // Ping de ejemplo (no imprescindible ahora)
    suspend fun ping(): Boolean = runCatching { api.ping() }
        .map { it.isSuccessful }
        .getOrDefault(false)

    // Devuelve una lista mock para compilar y mostrar algo
    suspend fun fetchPosts(): List<PostDto> {
        delay(300) // simula red
        return listOf(
            PostDto(1, "Hola Freyja"),
            PostDto(2, "Build estable en CI"),
            PostDto(3, "Compose funcionando ✨")
        )
    }
}
