cd ~/FreyjaBeta

# 1) Reescribe ChatViewModel.kt limpio
cat > app/src/main/java/com/angeluz/freyja/ChatViewModel.kt <<'KOT'
package com.angeluz.freyja

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.angeluz.freyja.model.ChatMessage
import java.util.concurrent.atomic.AtomicLong

class ChatViewModel : ViewModel() {

    private val _messages = mutableStateListOf<ChatMessage>()
    val messages: List<ChatMessage> get() = _messages

    private val nextId = AtomicLong(1L)

    fun send(text: String) {
        _messages.add(ChatMessage(id = nextId.getAndIncrement(), text = text, mine = true))
        _messages.add(ChatMessage(id = nextId.getAndIncrement(), text = "Echo: $text", mine = false))
    }
}
KOT

# (Opcional) Reconfirma ChatScreen correcto por si hubo conflictos previos
cat > app/src/main/java/com/angeluz/freyja/ui/screens/ChatScreen.kt <<'KOT'
package com.angeluz.freyja.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.angeluz.freyja.ChatViewModel
import com.angeluz.freyja.model.ChatMessage

@Composable
fun ChatScreen(vm: ChatViewModel = viewModel()) {
    var input by remember { mutableStateOf(TextFieldValue("")) }
    val messages: List<ChatMessage> = vm.messages

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
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
KOT

# 2) Commit + push protegido a main
git add app/src/main/java/com/angeluz/freyja/ChatViewModel.kt app/src/main/java/com/angeluz/freyja/ui/screens/ChatScreen.kt
git commit -m "fix(chat): limpiar ChatViewModel.kt contaminado y asegurar ChatScreen correcto" || true
git fetch origin main
git rebase origin/main || { git add -A && git rebase --continue; }
git push --force-with-lease origin HEAD:main

# 3) Relanza CI y muestra el fallo/éxito
gh workflow run android-ci.yml --repo KingBrutblarg/FreyjaBeta
RID=$(gh run list --workflow="android-ci.yml" -L 1 --json databaseId --jq '.[0].databaseId' --repo KingBrutblarg/FreyjaBeta)
gh run watch "$RID" --exit-status --repo KingBrutblarg/FreyjaBeta || true

# 4) Si falla, imprime bloque Kotlin completo
JID=$(gh run view "$RID" --json jobs --jq '.jobs[] | select(.conclusion!="success") | .databaseId' --repo KingBrutblarg/FreyjaBeta | head -n1)
gh run view "$RID" --log --job "$JID" --repo KingBrutblarg/FreyjaBeta | sed -n '/> Task :app:compileReleaseKotlin/,/FAILURE:/p'