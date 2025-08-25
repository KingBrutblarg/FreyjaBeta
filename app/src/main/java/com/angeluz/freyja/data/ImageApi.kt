package com.angeluz.freyja.data

import retrofit2.http.Body
import retrofit2.http.POST

// Request/Response mínimos (ajústalos a tu backend real)
data class ImageGenRequest(
    val prompt: String,
    val key: String   // pasamos la clave aquí (o el backend puede leerla por header)
)

data class ImageGenReply(
    val url: String? = null,     // si el backend devuelve URL
    val base64: String? = null   // o si devuelve imagen en base64
)

interface ImageApi {
    @POST("image/generate")
    suspend fun generate(@Body req: ImageGenRequest): ImageGenReply
}

// Extendemos el mismo RetrofitProvider para exponer "images"
val retrofit by lazy { RetrofitProvider.api } // solo para importar el type
object ImagesProvider {
    val api: ImageApi by lazy {
        // Reusamos el mismo Retrofit base (cambia la baseUrl en RetrofitProvider si tu imagen va a otro host)
        retrofit2.Retrofit.Builder()
            .baseUrl("https://TU_BACKEND/") // <-- cambia si tu servicio de imágenes vive en otro host
            .addConverterFactory(retrofit2.converter.moshi.MoshiConverterFactory.create())
            .build()
            .create(ImageApi::class.java)
    }
}
