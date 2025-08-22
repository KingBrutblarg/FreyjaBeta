package com.angeluz.freyja

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

object ImageMaker {

    private val client by lazy {
        OkHttpClient.Builder()
            .callTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()
    }
    private val JSON = "application/json; charset=utf-8".toMediaType()

    // --------- AUTOMATIC1111 (txt2img) ---------
    suspend fun a1111Txt2Img(
        ctx: Context,
        baseUrl: String,      // ej: "http://127.0.0.1:7860"
        prompt: String,
        steps: Int = 25,
        width: Int = 512,
        height: Int = 512
    ): Uri? = withContext(Dispatchers.IO) {
        val url = "$baseUrl/sdapi/v1/txt2img"
        val bodyJson = """
            {
              "prompt": "${prompt.replace("\"","\\\"")}",
              "steps": $steps,
              "width": $width,
              "height": $height
            }
        """.trimIndent()

        val req = Request.Builder()
            .url(url)
            .post(bodyJson.toRequestBody(JSON))
            .build()

        client.newCall(req).execute().use { resp ->
            if (!resp.isSuccessful) return@use null
            val json = resp.body?.string() ?: return@use null
            // Respuesta típica: {"images":["<base64>",...], ...}
            val base64 = Regex("\"images\"\\s*:\\s*\\[\\s*\"([^\"]+)\"")
                .find(json)?.groupValues?.getOrNull(1) ?: return@use null
            val bytes = Base64.decode(base64, Base64.DEFAULT)
            val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            return@use saveToGallery(ctx, bmp, "freyja_${System.currentTimeMillis()}.png")
        }
    }

    // --------- Stability (imagen binaria) ---------
    suspend fun stabilityTxt2Img(
        ctx: Context,
        apiKey: String,
        prompt: String,
        model: String = "stable-diffusion-v1-5"
    ): Uri? = withContext(Dispatchers.IO) {
        val url = "https://api.stability.ai/v1/generation/$model/text-to-image"
        val json = """
            {"text_prompts":[{"text":"${prompt.replace("\"","\\\"")}","weight":1.0}],
             "cfg_scale":7,"height":512,"width":512,"samples":1,"steps":30}
        """.trimIndent()

        val req = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Accept", "image/png")
            .post(json.toRequestBody(JSON))
            .build()

        client.newCall(req).execute().use { resp ->
            if (!resp.isSuccessful) return@use null
            val bytes = resp.body?.bytes() ?: return@use null
            val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            return@use saveToGallery(ctx, bmp, "freyja_${System.currentTimeMillis()}.png")
        }
    }

    // --------- Guardado en Galería (MediaStore; no requiere permisos extras) ---------
    private fun saveToGallery(ctx: Context, bmp: Bitmap, name: String): Uri? {
        val resolver = ctx.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, name)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.IS_PENDING, 1)
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Freyja")
            }
        }

        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            ?: return null

        // Stream no nulo y autocierre seguro
        resolver.openOutputStream(uri)?.use { out ->
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out)
        } ?: return null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val done = ContentValues().apply { put(MediaStore.Images.Media.IS_PENDING, 0) }
            resolver.update(uri, done, null, null)
        }
        return uri
    }
}