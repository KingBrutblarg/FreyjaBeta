package com.angeluz.freyja.ui.screens

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.angeluz.freyja.ChatViewModel
import com.angeluz.freyja.model.ChatMessage

@Composable
fun ChatScreen(vm: ChatViewModel = viewModel()) {
    var input by remember { mutableStateOf(TextFieldValue("")) }
    val messages: List<ChatMessage> = vm.messages
    val context = LocalContext.current

    val attachmentsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            runCatching {
                context.contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            vm.processFile(it)
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                reverseLayout = true
            ) {
                items(
                    items = messages.asReversed(),
                    key = { it.id }
                ) { msg ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
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

            Spacer(Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Escribe…") }
                )
                Spacer(Modifier.width(8.dp))
                Button(onClick = {
                    attachmentsLauncher.launch(arrayOf("text/*", "application/pdf"))
                }) {
                    Text("Adjuntar")
                }
                Spacer(Modifier.width(8.dp))
                Button(onClick = {
                    val msg = input.text.trim()
                    if (msg.isNotEmpty()) {
                        vm.send(msg)
                        input = TextFieldValue("")
                    }
                }) {
                    Text("Enviar")
                }
            }
        }
    }
}
