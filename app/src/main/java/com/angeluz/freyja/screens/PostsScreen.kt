import com.angeluz.freyja.model.PostDto
package com.angeluz.freyja.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.angeluz.freyja.ApiViewModel
import com.angeluz.freyja.data.PostDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostsScreen(
    modifier: Modifier = Modifier,
    vm: ApiViewModel = viewModel()
) {
    LaunchedEffect(Unit) { vm.loadPosts() }

    val posts by vm.posts.collectAsStateWithLifecycle()
    val loading by vm.isLoading.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Freyja • Posts") })
        }
    ) { padding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                loading -> {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
                posts.isEmpty() -> {
                    Text(
                        "No hay datos aún. Revisa tu API_BASE_URL.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(posts) { p ->
                            PostCard(p)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PostCard(post: PostDto) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = "#${post.id}  ${post.title}",
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = post.body,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
