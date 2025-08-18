package com.angeluz.freyja

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * Orquestador híbrido de salida de voz:
 *  - TERMUX  -> termux-tts-speak
 *  - REMOTE  -> POST a un endpoint (JSON {"text": "..."})
 *  - NATIVE  -> TtsManager (Android)
 */
class HybridInvoker(
    private val context: Context,
    private val tts: TtsManager
) {

    private val client by lazy { OkHttpClient() }
    private val json = "application/json; charset=utf-8".toMediaType()

    suspend fun speak(text: String): Result<String> {
        val mode = Prefs.modeFlow(context).first()
        return when (mode) {
            SpeakMode.TERMUX -> speakWithTermux(text)
            SpeakMode.REMOTE -> speakRemote(text)
            SpeakMode.NATIVE -> speakNative(text)
        }
    }

    private fun speakNative(text: String): Result<String> = runCatching {
        tts.speak(text)
        "NATIVE"
    }

    private fun speakWithTermux(text: String): Result<String> = runCatching {
        check(isTermuxInstalled()) { "Termux no instalado" }

        val escaped = text.replace("'", "\\'")
        val intent = Intent("com.termux.RUN_COMMAND").apply {
            setClassName("com.termux", "com.termux.app.RunCommandService")
            putExtra(
                "com.termux.RUN_COMMAND_PATH",
                "/data/data/com.termux/files/usr/bin/bash"
            )
            putExtra(
                "com.termux.RUN_COMMAND_ARGUMENTS",
                arrayOf("-lc", "termux-tts-speak '$escaped'")
            )
            putExtra("com.termux.RUN_COMMAND_BACKGROUND", true)
            putExtra(
                "com.termux.RUN_COMMAND_WORKDIR",
                "/data/data/com.termux/files/home"
            )
        }
        context.startService(intent)
        "TERMUX"
    }

    private suspend fun speakRemote(text: String): Result<String> = runCatching {
        val url = Prefs.urlFlow(context).first().ifEmpty {
            throw IllegalStateException("URL remota no configurada")
        }
        val token = Prefs.tokenFlow(context).first()

        val body = """{"text":${text.asJsonString()}}""".toRequestBody(json)
        val req = Request.Builder()
            .url(url.ensureHttpScheme())
            .addHeader("Accept", "application/json")
            .apply { if (token.isNotEmpty()) addHeader("Authorization", "Bearer $token") }
            .post(body)
            .build()

        client.newCall(req).execute().use { rsp ->
            if (!rsp.isSuccessful) error("HTTP ${rsp.code}")
        }
        "REMOTE"
    }

    private fun isTermuxInstalled(): Boolean =
        try {
            context.packageManager.getPackageInfo("com.termux", 0)
            true
        } catch (_: PackageManager.NameNotFoundException) {
            false
        }

    // Helpers
    private fun String.ensureHttpScheme(): String =
        if (startsWith("http://") || startsWith("https://")) this
        else "http://$this"

    private fun String.asJsonString(): String =
        "\"" + replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n") + "\""
}