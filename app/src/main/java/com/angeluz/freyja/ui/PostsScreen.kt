package com.angeluz.freyja.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.angeluz.freyja.ApiViewModel

@Composable
fun PostsScreen(
    vm: ApiViewModel = viewModel()
) {
    val list = remember(vm) { vm.posts } // SnapshotStateList<String>

    // Lanza la carga al entrar
    LaunchedEffect(Unit) { vm.fetchPosts() }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Freyja · Posts") })
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { vm.fetchPosts() },
                text = { Text("Recargar") }
            )
        }
    ) { padding ->
        if (list.isEmpty()) {
            // Estado vacío / cargando
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(list) { title ->
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}