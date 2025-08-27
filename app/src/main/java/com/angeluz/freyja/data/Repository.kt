package com.angeluz.freyja.data

class Repository(
    private val api: ApiService = RetrofitProvider.retrofit.create(ApiService::class.java)
) {
    suspend fun ping(): Boolean = runCatching { api.ping() }
        .map { it.isSuccessful }
        .getOrDefault(false)
}
