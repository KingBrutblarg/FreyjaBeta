package com.angeluz.freyja.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.angeluz.freyja.ChatViewModel

@Composable
fun FreyjaScreen(vm: ChatViewModel) {
    var prompt by remember { mutableStateOf("") }
    val reply by vm.reply.collectAsState(initial = "")
    val loading by vm.loading.collectAsState(initial = false)
    val error by vm.error.collectAsState(initial = null)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = prompt,
            onValueChange = { prompt = it },
            label = { Text(text = "Háblame, Ezlhan…") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { if (prompt.isNotBlank()) vm.send(prompt) },
            enabled = !loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = if (loading) "Invocando…" else "Invocar a Freyja")
        }

        if (reply.isNotBlank()) {
            Text(text = reply)
        }

        error?.let {
            Text(text = "Error: $it", color = MaterialTheme.colorScheme.error)
        }
    }
}
