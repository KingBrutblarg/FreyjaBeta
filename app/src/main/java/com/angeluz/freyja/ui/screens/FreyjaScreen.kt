package com.angeluz.freyja.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.angeluz.freyja.Prefs
import com.angeluz.freyja.data.remote.ChatRequest
import com.angeluz.freyja.data.remote.RetrofitProvider
import com.angeluz.freyja.ui.ChatViewModel
import com.angeluz.freyja.ui.theme.RuneMist
import kotlinx.coroutines.launch

@Composable
private fun MistLayer(infinite: InfiniteTransition, baseAlpha: Float, blurDp: Float) {
    val a by infinite.animateFloat(
        initialValue = 0.2f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(baseAlpha * a)
            .blur(blurDp.dp)
            .background(
                Brush.radialGradient(
                    colors = listOf(RuneMist, Color.Transparent)
                )
            )
    )
}

@Composable
fun FreyjaScreen(vm: ChatViewModel) {
    val ctx = LocalContext.current
    val reply by vm.reply.collectAsState(initial = "")

    var prompt by remember { mutableStateOf("") }
    var temp by remember { mutableFloatStateOf(0.7f) }
    var debug by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    var showUnlock by remember { mutableStateOf(false) }
    var unlocked by remember { mutableStateOf(Prefs.isUnlocked(ctx)) }

    val scope = rememberCoroutineScope()
    val infinite = rememberInfiniteTransition(label = "mist")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Fondo brumoso
        MistLayer(infinite, baseAlpha = 0.20f, blurDp = 22f)
        MistLayer(infinite, baseAlpha = 0.10f, blurDp = 36f)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Encabezado
            Text(
                "La Voz de Freyja",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )

            // Símbolo rúnico / botón de desbloqueo
            if (unlocked) {
                Text(
                    text = "ᚠᚱᛖᛃᛃᚨ ✧",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.alpha(0.85f)
                )
            } else {
                OutlinedButton(onClick = { showUnlock = true }) {
                    Text("Desbloquear bóveda (1226)")
                }
            }

            // Prompt
            OutlinedTextField(
                value = prompt,
                onValueChange = { prompt = it },
                label = { Text("Háblame, Ezlhan…") },
                modifier = Modifier.fillMaxWidth()
            )

            // Slider de temperatura
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Temperatura: ${"%.2f".format(temp)}")
                Slider(
                    value = temp,
                    onValueChange = { temp = it.coerceIn(0f, 1f) },
                    valueRange = 0f..1f
                )
            }

            // Conmutador debug (muestra JSON o solo reply)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Modo debug (JSON)")
                Switch(checked = debug, onCheckedChange = { debug = it })
            }

            // Botón “Invocar”
            Button(
                onClick = {
                    if (prompt.isBlank()) return@Button
                    loading = true
                    error = null
                    scope.launch {
                        try {
                            val res = RetrofitProvider.api.chat(
                                ChatRequest(prompt, temp.toDouble())
                            )
                            if (debug) {
                                vm.send("DEBUG → ${res.reply}")
                            } else {
                                vm.send(res.reply)
                            }
                        } catch (e: Exception) {
                            error = e.message
                        } finally {
                            loading = false
                        }
                    }
                },
                enabled = !loading,
                modifier = Modifier.fillMaxWidth()
            ) { Text(if (loading) "Invocando…" else "Invocar a Freyja") }

            // Respuesta
            if (reply.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = reply,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // Error
            error?.let {
                Text("Error: $it", color = MaterialTheme.colorScheme.error)
            }
        }
    }

    // Diálogo de desbloqueo (1226)
    if (showUnlock) {
        UnlockDialog(
            context = ctx,
            onDismiss = { showUnlock = false },
            onUnlocked = {
                unlocked = true
                showUnlock = false
            }
        )
    }
}
