package com.angeluz.freyja.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.angeluz.freyja.ChatViewModel
import androidx.compose.foundation.layout.Arrangement

@Composable
fun ChatScreen(vm: ChatViewModel = viewModel()) {
    // Cadena simple guardada en recomposiciones / rotaciones
    var input by rememberSaveable { mutableStateOf("") }

    // Observa los mensajes de la VM de forma lifecycle-aware
    val messages by vm.messages.collectAsStateWithLifecycle(emptyList())

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
        ) {

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                reverseLayout = true // muestra lo más nuevo abajo
            ) {
                items(messages) { msg ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = if (msg.mine) Arrangement.End else Arrangement.Start
                    ) {
                        Surface(
                            color = if (msg.mine)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.secondaryContainer,
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text(
                                text = msg.text,
                                modifier = Modifier.padding(10.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Escribe…") },
                    singleLine = true
                )
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = {
                        val msg = input.trim()
                        if (msg.isNotEmpty()) {
                            vm.send(msg)
                            input = ""
                        }
                    }
                ) {
                    Text("Enviar")
                }
            }
        }
    }
}