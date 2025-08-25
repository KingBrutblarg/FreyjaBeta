package com.angeluz.freyja.data

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

// Request/Response mínimos (ajústalos a tu backend real)
data class ImageGenRequest(
    val prompt: String,
    val key: String   // también puedes mover esto a un header en el backend
)

data class ImageGenReply(
    val url: String? = null,      // si el backend devuelve URL
    val base64: String? = null    // o si devuelve imagen en base64
)

interface ImageApi {
    @POST("image/generate")
    suspend fun generate(@Body req: ImageGenRequest): ImageGenReply
}

object ImagesProvider {
    val api: ImageApi by lazy {
        // Usa el mismo host que el chat, si es el mismo (cámbialo si va a otro servicio)
        Retrofit.Builder()
            .baseUrl("https://TU_BACKEND/") // TODO: cambia si tu generador vive en otro host
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(ImageApi::class.java)
    }
}
