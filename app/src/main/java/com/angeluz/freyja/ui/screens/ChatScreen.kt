package com.angeluz.freyja.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.angeluz.freyja.ChatViewModel

@Composable
fun ChatScreen(vm: ChatViewModel = ChatViewModel()) {
    var prompt by remember { mutableStateOf("") }
    val reply by vm.reply.collectAsState()
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = prompt,
            onValueChange = { prompt = it },
            label = { Text("Háblame, Ezlhan…") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = { vm.send(prompt.trim()) },
            enabled = !loading && prompt.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (loading) "Invocando…" else "Invocar a Freyja")
        }
        when {
            error != null -> Text(
                "Error: $error",
                color = MaterialTheme.colorScheme.error
            )
            reply != null -> Text("Freyja dice: ${reply!!}")
        }
    }
}