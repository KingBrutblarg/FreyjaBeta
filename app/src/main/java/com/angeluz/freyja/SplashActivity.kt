package com.angeluz.freyja

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.angeluz.freyja.ui.theme.FreyjaTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : ComponentActivity() {

    private val requestNotif = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* resultado opcional */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // UI de splash
        setContent {
            FreyjaTheme {
                SplashContent()
            }
        }

        // Permiso de notificaciones (Android 13+)
        if (Build.VERSION.SDK_INT >= 33) {
            requestNotif.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        // Inicia el Foreground Service
        startService(Intent(this, FreyjaService::class.java))

        // Decide destino de forma asíncrona (sin LaunchedEffect)
        lifecycleScope.launch {
            // pequeña pausa “mística” para que se vea el splash
            delay(1200)

            val unlocked = Prefs.isUnlocked(this@SplashActivity)
            val next = if (unlocked) MainActivity::class.java else MainActivity::class.java
            // ↑ TODO: cuando tengas pantalla de candado, cámbiala en el else

            startActivity(Intent(this@SplashActivity, next))
            finish()
        }
    }
}

@Composable
private fun SplashContent() {
    Surface {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}