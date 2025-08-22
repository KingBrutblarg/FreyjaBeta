package com.angeluz.freyja.ui.screens

import androidx.compose.animation.core.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.angeluz.freyja.data.remote.ChatRequest
import com.angeluz.freyja.data.remote.RetrofitProvider
import com.angeluz.freyja.ui.ChatViewModel
import com.angeluz.freyja.ui.theme.RuneMist
import kotlinx.coroutines.launch

@Composable private fun MistLayer(infinite: InfiniteTransition, baseAlpha: Float, blurDp: Float) {
    val a by infinite.animateFloat(
        initialValue = 0.2f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(6000, easing = FastOutSlowInEasing), RepeatMode.Reverse)
    )
    Box(
        Modifier.fillMaxSize().alpha(baseAlpha * a).blur(blurDp.dp).background(
            Brush.radialGradient(listOf(RuneMist, Color.Transparent))
        )
    )
}

@Composable
fun FreyjaScreen(vm: ChatViewModel) {
    val reply by vm.reply.collectAsState(initial = "")
    var prompt by remember { mutableStateOf("") }
    var temp by remember { mutableFloatStateOf(0.7f) }
    var debug by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val infinite = rememberInfiniteTransition(label = "mist")

    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        MistLayer(infinite, 0.20f, 22f)
        MistLayer(infinite, 0.10f, 36f)

        Column(
            Modifier.fillMaxSize().padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("La Voz de Freyja", style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)

            OutlinedTextField(
                value = prompt, onValueChange = { prompt = it },
                label = { Text("Háblame, Ezlhan…") },
                modifier = Modifier.fillMaxWidth()
            )

            Column(Modifier.fillMaxWidth()) {
                Text("Temperatura: ${"%.2f".format(temp)}")
                Slider(value = temp, onValueChange = { temp = it.coerceIn(0f,1f) }, valueRange = 0f..1f)
            }

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Modo debug (JSON)")
                Switch(checked = debug, onCheckedChange = { debug = it })
            }

            Button(
                onClick = {
                    if (prompt.isBlank()) return@Button
                    loading = true; error = null
                    scope.launch {
                        try {
                            if (debug) {
                                val res = RetrofitProvider.api.chat(ChatRequest(prompt, temp.toDouble()))
                                vm.send("DEBUG → ${res.reply}")
                            } else {
                                val res = RetrofitProvider.api.chat(ChatRequest(prompt, temp.toDouble()))
                                vm.send(res.reply)
                            }
                        } catch (e: Exception) { error = e.message }
                        finally { loading = false }
                    }
                },
                enabled = !loading, modifier = Modifier.fillMaxWidth()
            ) { Text(if (loading) "Invocando…" else "Invocar a Freyja") }

            if (reply.isNotBlank()) {
                Card(
                    Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(4.dp),
                    shape = RoundedCornerShape(16.dp)
                ) { Text(reply, Modifier.padding(16.dp)) }
            }
            if (error != null) Text("Error: $error")
            Spacer(Modifier.height(6.dp))
        }
    }
}
