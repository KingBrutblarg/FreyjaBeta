package com.angeluz.freyja
object LlamaBridge {
    init { System.loadLibrary("tauriel") }
    external fun initModel(path: String, ctxTokens: Int): Boolean
    external fun complete(prompt: String, maxTokens: Int, temp: Float): String
}