package com.angeluz.freyja.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
<<<<<<< HEAD
import androidx.compose.ui.Alignment
=======
>>>>>>> fb03f9f (feat(chat): UI ChatScreen + ajustes en ChatViewModel)
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.angeluz.freyja.ChatViewModel

@Composable
fun ChatScreen(vm: ChatViewModel = ChatViewModel()) {
    var prompt by remember { mutableStateOf("") }
<<<<<<< HEAD
    val reply by vm.reply.collectAsState()
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
=======
    val reply by vm.reply.collectAsState(null)
    val loading by vm.loading.collectAsState(false)
    val error by vm.error.collectAsState(null)

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
>>>>>>> fb03f9f (feat(chat): UI ChatScreen + ajustes en ChatViewModel)
    ) {
        OutlinedTextField(
            value = prompt,
            onValueChange = { prompt = it },
            label = { Text("Háblame, Ezlhan…") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
<<<<<<< HEAD
            onClick = { vm.send(prompt.trim()) },
            enabled = !loading && prompt.isNotBlank(),
=======
            onClick = { if (prompt.isNotBlank()) vm.ask(prompt) },
            enabled = !loading,
>>>>>>> fb03f9f (feat(chat): UI ChatScreen + ajustes en ChatViewModel)
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (loading) "Invocando…" else "Invocar a Freyja")
        }
<<<<<<< HEAD
        when {
            error != null -> Text(
                "Error: $error",
                color = MaterialTheme.colorScheme.error
            )
            reply != null -> Text("Freyja dice: ${reply!!}")
        }
    }
}
=======
        error?.let { Text("Error: $it", color = MaterialTheme.colorScheme.error) }
        reply?.let { Text("Freyja dice: $it") }
    }
}
>>>>>>> fb03f9f (feat(chat): UI ChatScreen + ajustes en ChatViewModel)
