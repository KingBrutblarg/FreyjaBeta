package com.angeluz.freyja

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.angeluz.freyja.screens.PostsScreen
import com.angeluz.freyja.ui.theme.YourAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YourAppTheme {
                MaterialTheme {
                    PostsScreen()
                }
            }
        }
    }
}
