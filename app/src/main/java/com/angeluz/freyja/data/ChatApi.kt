package com.angeluz.freyja.data

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

<<<<<<< HEAD
// --- Request y Reply (puedes ajustarlos según tu backend)
=======
// --- Modelos mínimos (ajústalos a tu backend real)
>>>>>>> 59b2da0 (feat: Retrofit ChatApi + ChatViewModel que llama a API; permiso INTERNET y deps)
data class ChatRequest(val prompt: String)
data class ChatReply(val text: String)

// --- API REST
interface ChatApi {
    @POST("chat")
    suspend fun chat(@Body req: ChatRequest): ChatReply
}

// --- Retrofit Provider
object RetrofitProvider {
    val api: ChatApi by lazy {
        Retrofit.Builder()
<<<<<<< HEAD
            .baseUrl("https://TU_BACKEND/") // TODO: cámbialo por tu endpoint real
=======
            .baseUrl("https://TU_BACKEND/") // TODO: pon tu endpoint real (termina en /)
>>>>>>> 59b2da0 (feat: Retrofit ChatApi + ChatViewModel que llama a API; permiso INTERNET y deps)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(ChatApi::class.java)
    }
<<<<<<< HEAD
}
=======
}
>>>>>>> 59b2da0 (feat: Retrofit ChatApi + ChatViewModel que llama a API; permiso INTERNET y deps)
