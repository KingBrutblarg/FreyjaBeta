package com.angeluz.freyja.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.angeluz.freyja.ImageGenViewModel

@Composable
fun ImageGenScreen(vm: ImageGenViewModel = ImageGenViewModel()) {
    var prompt by remember { mutableStateOf("") }
    val url by vm.url.collectAsState(null)
    val bmp by vm.bitmap.collectAsState(null)
    val loading by vm.loading.collectAsState(false)
    val error by vm.error.collectAsState(null)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Generador de imágenes", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = prompt,
            onValueChange = { prompt = it },
            label = { Text("Describe la escena") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = { if (prompt.isNotBlank()) vm.generate(prompt) },
            enabled = !loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (loading) "Generando…" else "Generar imagen")
        }

        error?.let { Text("Error: $it", color = MaterialTheme.colorScheme.error) }

        url?.let {
            AsyncImage(model = it, contentDescription = null, modifier = Modifier.fillMaxWidth())
        }
        bmp?.let {
            Image(bitmap = it.asImageBitmap(), contentDescription = null, modifier = Modifier.fillMaxWidth())
        }
    }
}
