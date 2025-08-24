package com.angeluz.freyja

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.angeluz.freyja.ui.screens.FreyjaScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Evita trabajo pesado en UI aquí; inicializa perezoso
        setContent {
            MaterialTheme {
                FreyjaScreen(vm = ChatViewModel())
            }
        }
    }
}
