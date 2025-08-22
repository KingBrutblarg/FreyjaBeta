package com.angeluz.freyja

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import kotlin.math.sin
import kotlin.random.Random

class SplashActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startVoiceServiceSafe()

        setContent {
            MaterialTheme {
                RunicSplash(
                    onFinished = {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }

    private fun startVoiceServiceSafe() {
        val svc = Intent(this, VoiceService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(this, svc)
        } else {
            startService(svc)
        }
    }
}

@Composable
private fun RunicSplash(onFinished: () -> Unit) {
    val ctx = LocalContext.current
    var unlocked by remember { mutableStateOf(Prefs.isUnlocked(ctx)) }
    var showUnlock by remember { mutableStateOf(false) }

    // Timer para salir del splash
    LaunchedEffect(Unit) {
        // Dejamos ver un poco la animación
        kotlinx.coroutines.delay(1800)
        onFinished()
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)
    ) {
        if (unlocked) {
            BovedaRunica()         // Fondo de estrellas
        } else {
            BrumaRunica()          // Niebla rúnica animada
        }

        // Título / texto
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                if (unlocked) "🌌 Guardia de Freyja activa"
                else "🌬️ Invocando la Voz de Freyja…",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White.copy(alpha = 0.9f)
            )
        }

        // Símbolo de la Guardia (cuando está activada)
        if (unlocked) {
            GuardiaPulsante(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(48.dp)
            )
        }

        // Botón sutil de invocación (arriba-derecha)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            IconButton(
                modifier = Modifier.align(Alignment.TopEnd),
                onClick = { showUnlock = true }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_rune_key),
                    contentDescription = "Invocación secreta",
                    tint = Color.White.copy(alpha = 0.6f)
                )
            }
        }

        // Diálogo de desbloqueo (Llave 1226)
        if (showUnlock) {
            UnlockDialog(
                onDismiss = { showUnlock = false },
                onSubmit = { code ->
                    if (code.trim() == "1226") {
                        Prefs.setUnlocked(ctx, true)
                        unlocked = true
                        Toast.makeText(ctx, "✨ Guardia de Freyja activada", Toast.LENGTH_SHORT).show()
                        showUnlock = false
                    } else {
                        Toast.makeText(ctx, "Clave incorrecta", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }
}

@Composable
private fun UnlockDialog(
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    var key by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Llave Rúnica") },
        text = {
            Column {
                Text("Introduce la llave para invocar a la Guardia.")
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = key,
                    onValueChange = { key = it },
                    singleLine = true,
                    placeholder = { Text("••••") },
                    visualTransformation = PasswordVisualTransformation()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onSubmit(key) }) { Text("Invocar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

/* ---------- Animaciones ---------- */

@Composable
private fun BrumaRunica() {
    // 3 capas de “niebla” moviéndose lentamente
    val t1 by rememberInfiniteTransition().animateFloat(
        initialValue = 0f, targetValue = 2 * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(animation = tween(8000, easing = LinearEasing))
    )
    val t2 by rememberInfiniteTransition().animateFloat(
        initialValue = 0f, targetValue = 2 * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(animation = tween(11000, easing = LinearEasing))
    )
    val t3 by rememberInfiniteTransition().animateFloat(
        initialValue = 0f, targetValue = 2 * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(animation = tween(13500, easing = LinearEasing))
    )

    Canvas(Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Fondo tenue púrpura/azul
        drawRect(brush = Brush.verticalGradient(
            0f to Color(0xFF0B0B12),
            1f to Color(0xFF121226)
        ))

        // Capa 1 (suave)
        val x1 = w * (0.5f + 0.15f * sin(t1))
        val y1 = h * (0.5f + 0.10f * sin(t1 * 0.7f))
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF9B87F5).copy(alpha = 0.14f), Color.Transparent),
                center = androidx.compose.ui.geometry.Offset(x1, y1),
                radius = h * 0.65f
            ),
            radius = h * 0.65f,
            center = androidx.compose.ui.geometry.Offset(x1, y1)
        )

        // Capa 2 (fría)
        val x2 = w * (0.3f + 0.2f * sin(t2 * 1.2f))
        val y2 = h * (0.6f + 0.15f * sin(t2 * 0.9f))
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF6ED0FF).copy(alpha = 0.12f), Color.Transparent),
                center = androidx.compose.ui.geometry.Offset(x2, y2),
                radius = h * 0.55f
            ),
            radius = h * 0.55f,
            center = androidx.compose.ui.geometry.Offset(x2, y2)
        )

        // Capa 3 (fuego tenue)
        val x3 = w * (0.7f + 0.18f * sin(t3 * 0.8f))
        val y3 = h * (0.4f + 0.14f * sin(t3 * 1.1f))
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFF8866).copy(alpha = 0.10f), Color.Transparent),
                center = androidx.compose.ui.geometry.Offset(x3, y3),
                radius = h * 0.60f
            ),
            radius = h * 0.60f,
            center = androidx.compose.ui.geometry.Offset(x3, y3)
        )
    }
}

@Composable
private fun BovedaRunica(starCount: Int = 140) {
    // Estrellas fijas durante la vida del Composable
    val stars = remember {
        List(starCount) {
            Star(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                r = 0.5f + Random.nextFloat() * 1.5f,
                twinkle = 0.6f + Random.nextFloat() * 0.4f
            )
        }
    }

    val twinkleAnim by rememberInfiniteTransition().animateFloat(
        initialValue = 0f, targetValue = 2 * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(animation = tween(3000, easing = LinearEasing))
    )

    Canvas(Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Fondo “bóveda”
        drawRect(brush = Brush.verticalGradient(
            0f to Color(0xFF0A0A11),
            0.5f to Color(0xFF10102A),
            1f to Color(0xFF130B1A)
        ))

        // Nebulosa central
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF7048E8).copy(alpha = 0.20f), Color.Transparent),
                center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.45f),
                radius = h * 0.55f
            ),
            radius = h * 0.55f,
            center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.45f)
        )

        // Estrellas con parpadeo
        val base = Color.White
        stars.forEachIndexed { i, s ->
            val alpha = 0.4f + 0.6f * (0.5f + 0.5f * kotlin.math.sin(twinkleAnim + i * 0.17f).toFloat()) * s.twinkle
            drawCircle(
                color = base.copy(alpha = alpha),
                radius = s.r,
                center = androidx.compose.ui.geometry.Offset(s.x * w, s.y * h)
            )
        }
    }
}

private data class Star(val x: Float, val y: Float, val r: Float, val twinkle: Float)

@Composable
private fun GuardiaPulsante(modifier: Modifier = Modifier) {
    val pulse by rememberInfiniteTransition().animateFloat(
        initialValue = 0.9f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val glow by rememberInfiniteTransition().animateFloat(
        initialValue = 0.35f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(modifier, contentAlignment = Alignment.Center) {
        // Halo
        Canvas(Modifier.fillMaxSize(0.5f)) {
            val w = size.width
            val h = size.height
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF8A7CFE).copy(alpha = glow), Color.Transparent)
                ),
                radius = minOf(w, h) * 0.45f
            )
        }
        // Símbolo
        Image(
            painter = painterResource(id = R.drawable.ic_guardia_freyja),
            contentDescription = "Guardia de Freyja",
            modifier = Modifier.fillMaxSize(pulse * 0.35f)
        )
    }
}