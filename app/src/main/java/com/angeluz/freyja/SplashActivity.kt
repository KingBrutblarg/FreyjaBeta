package com.angeluz.freyja

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.first

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }

        // Decide destino de forma asíncrona
        LaunchedEffect(Unit) {
            val unlocked = Prefs.run { this@SplashActivity.isUnlockedFlow.first() }
            val next = if (unlocked) MainActivity::class.java else MainActivity::class.java
            // ↑ cuando tengas pantalla de bloqueo/candado, cámbiala aquí
            startActivity(Intent(this@SplashActivity, next))
            finish()
        }
    }
}
