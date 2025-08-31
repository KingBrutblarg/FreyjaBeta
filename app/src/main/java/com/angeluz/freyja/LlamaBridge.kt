package com.angeluz.freyja

object LlamaBridge {
    init { System.loadLibrary("llama") }  // Carga libllama.so (nombre del target en CMake)
    external fun initModel(path: String): Boolean
    external fun infer(prompt: String): String
}
