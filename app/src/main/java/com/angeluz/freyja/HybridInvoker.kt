package com.angeluz.freyja

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

/**
 * Invocador híbrido: decide a qué backend hablar según Pref.mode.
 */
object HybridInvoker {

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .callTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private val JSON = "application/json; charset=utf-8".toMediaType()

    data class ApiResult(
        val code: Int,
        val body: String?,
        val ok: Boolean
    )

    // -------- util HTTP --------
    private suspend fun postJson(
        url: String,
        jsonBody: String,
        headers: Map<String, String> = emptyMap()
    ): ApiResult = withContext(Dispatchers.IO) {
        val body = jsonBody.toRequestBody(JSON)
        val reqBuilder = Request.Builder().url(url).post(body)
        headers.forEach { (k, v) -> reqBuilder.header(k, v) } // header() sobre el Builder
        client.newCall(reqBuilder.build()).execute().use { resp ->
            val bodyStr = resp.body?.string()
            ApiResult(resp.code, bodyStr, resp.isSuccessful)
        }
    }

    // -------- API pública de ejemplo --------
    suspend fun speak(ctx: android.content.Context, payloadJson: String): ApiResult {
        val mode = Pref.modeFlow(ctx).first()
        return when (mode) {
            SpeakMode.TERMUX -> {
                // ejemplo: servicio local en el teléfono
                postJson("http://127.0.0.1:11434/invoke", payloadJson)
            }
            SpeakMode.REMOTE -> {
                val url = Pref.urlFlow(ctx).first().ifBlank { "https://api.tu-servidor.com/invoke" }
                val token = Pref.tokenFlow(ctx).first()
                val headers = if (token.isNotBlank()) mapOf("Authorization" to "Bearer $token") else emptyMap()
                postJson(url, payloadJson, headers)
            }
            SpeakMode.NATIVE -> {
                // Si tu modo nativo no requiere red, devuelve OK simulado
                ApiResult(200, """{"status":"native-ok"}""", true)
            }
        }
    }

    // Fallback local->remoto
    suspend fun smartInvoke(
        ctx: android.content.Context,
        localUrl: String,
        remoteUrl: String,
        jsonBody: String,
        headersLocal: Map<String, String> = emptyMap(),
        headersRemote: Map<String, String> = emptyMap()
    ): ApiResult {
        runCatching {
            val r = postJson(localUrl, jsonBody, headersLocal)
            if (r.ok) return r
        }
        return postJson(remoteUrl, jsonBody, headersRemote)
    }
}