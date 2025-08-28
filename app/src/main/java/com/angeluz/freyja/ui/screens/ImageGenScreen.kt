package com.angeluz.freyja.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ImageGenScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        Text("ImageGen (placeholder)", style = MaterialTheme.typography.titleLarge)
        Text("Próximamente ✨")
    }
}
