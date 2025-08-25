package com.angeluz.freyja.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.angeluz.freyja.ChatViewModel

@Composable
fun ChatScreen(vm: ChatViewModel = viewModel()) {
    val input by vm.input.collectAsState()
    val reply by vm.reply.collectAsState()
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Freyja Chat", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = input,
            onValueChange = vm::onInputChange,
            label = { Text("Escribe tu mensaje") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Button(
            onClick = vm::send,
            enabled = !loading && input.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(if (loading) "Enviando…" else "Enviar")
        }

        if (error != null) {
            Text("Error: $error", color = MaterialTheme.colorScheme.error)
        }

        reply?.let {
            Surface(
                tonalElevation = 2.dp,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = it,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        Spacer(Modifier.weight(1f))
        Text(
            "Base URL: " + com.angeluz.freyja.BuildConfig.API_BASE_URL,
            style = MaterialTheme.typography.bodySmall
        )
    }
}