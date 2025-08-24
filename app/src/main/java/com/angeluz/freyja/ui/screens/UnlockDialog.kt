package com.angeluz.freyja.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.angeluz.freyja.Prefs

@Composable
fun UnlockDialog(
    context: Context,
    onDismiss: () -> Unit,
    onUnlocked: () -> Unit
) {
    val keyState = remember { mutableStateOf("") }
    val errorState = remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Llave Rúnica") },
        text = {
            Column {
                Text("Ingresa la clave para abrir la bóveda.")
                OutlinedTextField(
                    value = keyState.value,
                    onValueChange = { keyState.value = it },
                    label = { Text("Clave") },
                    visualTransformation = PasswordVisualTransformation()
                )
                errorState.value?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (keyState.value.trim() == "1226") {
                    Prefs.setUnlocked(context, true)
                    onUnlocked()
                } else {
                    errorState.value = "Clave incorrecta."
                }
            }) { Text("Desbloquear") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
