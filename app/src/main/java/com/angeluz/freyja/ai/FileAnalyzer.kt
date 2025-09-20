package com.angeluz.freyja.ai

/**
 * Analizador sencillo que genera resúmenes y pistas para archivos de texto o PDF.
 */
class FileAnalyzer {

    fun summarizeText(rawText: String): String {
        val normalized = rawText.replace(Regex("\s+"), " ").trim()
        if (normalized.isEmpty()) {
            return "Leí el archivo, pero no encontré texto que pudiera resumir."
        }

        val sentences = normalized.split(SENTENCE_REGEX)
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        val summary = sentences.take(MAX_SUMMARY_SENTENCES)
            .joinToString(separator = " ")
            .take(MAX_SUMMARY_CHARS)

        val highlights = rawText.lines()
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .take(MAX_HIGHLIGHTS)

        return buildString {
            appendLine("Resumen relámpago:")
            if (summary.isNotEmpty()) {
                appendLine(summary)
            } else {
                appendLine(highlights.firstOrNull() ?: normalized.take(MAX_SUMMARY_CHARS))
            }

            if (highlights.isNotEmpty()) {
                appendLine()
                appendLine("Puntos clave:")
                highlights.forEach { line ->
                    appendLine("• ${line.take(MAX_HIGHLIGHT_CHARS)}")
                }
            }

            appendLine()
            append("Extensión aproximada: ${normalized.length} caracteres.")
        }
    }

    fun describePdf(pageCount: Int?): String = when {
        pageCount == null ->
            "Pude abrir el PDF, pero no logré calcular cuántas páginas tiene. ¿Quieres intentar compartirlo como texto?"

        pageCount <= 0 ->
            "El PDF parece vacío o protegido. Si puedes convertirlo a texto plano podré ayudarte mejor."

        pageCount == 1 ->
            "Recibí un PDF de una página. Convierte su contenido a texto si deseas un resumen más profundo."

        else ->
            "Recibí un PDF de $pageCount páginas. Puedo darte pistas si me dices qué secciones te interesan o si lo exportas a texto."
    }

    companion object {
        private const val MAX_SUMMARY_SENTENCES = 3
        private const val MAX_SUMMARY_CHARS = 400
        private const val MAX_HIGHLIGHTS = 3
        private const val MAX_HIGHLIGHT_CHARS = 140
        private val SENTENCE_REGEX = Regex("(?<=[.!?])\\s+")
    }
}
