package com.angeluz.freyja.model

data class ChatMessage(
    val id: Long,
    val text: String,
    val mine: Boolean
)
