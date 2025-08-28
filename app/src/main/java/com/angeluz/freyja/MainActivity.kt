package com.angeluz.freyja

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.angeluz.freyja.ApiViewModel
import com.angeluz.freyja.screens.PostsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Home()
            }
        }
    }
}

@Composable
fun Home() {
    Column {
        Text("Hola Freyja ✨")
        PostsScreen(vm = ApiViewModel())
    }
}
