package com.angeluz.freyja

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import java.io.File
import java.io.InputStream
import java.io.OutputStream

object ModelStorage {
    private const val PREFS = "model_prefs"
    private const val KEY_TREE_URI = "models_tree_uri"

    fun saveModelsTreeUri(ctx: Context, uri: Uri) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putString(KEY_TREE_URI, uri.toString()).apply()
    }

    fun getModelsTreeUri(ctx: Context): Uri? {
        val s = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY_TREE_URI, null)
        return s?.let { Uri.parse(it) }
    }

    /** Busca el .gguf más reciente en la carpeta configurada (y subcarpetas). */
    fun findLatestGgufInTree(ctx: Context): Uri? {
        val tree = getModelsTreeUri(ctx) ?: return null
        val root = DocumentFile.fromTreeUri(ctx, tree) ?: return null
        var latest: Pair<Long, DocumentFile>? = null
        fun walk(dir: DocumentFile) {
            dir.listFiles().forEach { f ->
                if (f.isDirectory) walk(f)
                else if (f.name?.endsWith(".gguf", true) == true) {
                    val ts = f.lastModified()
                    if (latest == null || ts > latest!!.first) latest = ts to f
                }
            }
        }
        walk(root)
        return latest?.second?.uri
    }

    /** Copia→verifica→borra origen (si el provider lo permite). Devuelve ruta destino. */
    fun moveModelFromUri(ctx: Context, src: Uri): String? {
        val cr = ctx.contentResolver
        val name = queryDisplayName(ctx, src) ?: "model.gguf"
        val destDir = File(ctx.filesDir, "models").apply { mkdirs() }
        val dest = uniqueName(destDir, name)

        try {
            cr.openInputStream(src).use { `in` ->
                if (`in` == null) return null
                dest.outputStream().use { out -> copyStream(`in`, out) }
            }
        } catch (e: Exception) {
            Log.e("ModelStorage", "copy failed", e); return null
        }

        if (!dest.exists() || dest.length() == 0L) return null

        // intenta borrar origen
        try {
            val doc = DocumentFile.fromSingleUri(ctx, src)
            if (doc != null && doc.canWrite()) doc.delete() else cr.delete(src, null, null)
        } catch (_: Exception) {}

        dedupeSameNameAndSize(destDir, dest)
        return dest.absolutePath
    }

    fun latestModelFile(ctx: Context): File? {
        val dir = File(ctx.filesDir, "models")
        val all = dir.listFiles { f -> f.isFile && f.name.endsWith(".gguf", true) } ?: return null
        return all.maxByOrNull { it.lastModified() }
    }

    private fun copyStream(`in`: InputStream, out: OutputStream) {
        val buf = ByteArray(1024 * 1024)
        while (true) { val r = `in`.read(buf); if (r <= 0) break; out.write(buf, 0, r) }
        out.flush()
    }

    private fun queryDisplayName(ctx: Context, uri: Uri): String? {
        val proj = arrayOf(android.provider.OpenableColumns.DISPLAY_NAME)
        ctx.contentResolver.query(uri, proj, null, null, null)?.use { c ->
            val idx = c.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            if (idx >= 0 && c.moveToFirst()) return c.getString(idx)
        }
        return null
    }

    private fun uniqueName(dir: File, display: String): File {
        var base = display; var ext = ""
        val dot = display.lastIndexOf('.')
        if (dot > 0) { base = display.substring(0, dot); ext = display.substring(dot) }
        var candidate = File(dir, display); var n = 1
        while (candidate.exists()) { candidate = File(dir, "$base ($n)$ext"); n++ }
        return candidate
    }

    private fun dedupeSameNameAndSize(dir: File, imported: File) {
        val siblings = dir.listFiles()?.filter { it.isFile && it.nameWithoutExtension == imported.nameWithoutExtension } ?: return
        val sameSize = siblings.filter { it.length() == imported.length() }
        if (sameSize.size <= 1) return
        val sorted = sameSize.sortedByDescending { it.lastModified() }
        sorted.drop(1).forEach { it.delete() }
    }
}