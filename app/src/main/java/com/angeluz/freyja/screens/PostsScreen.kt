package com.angeluz.freyja.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.angeluz.freyja.ApiViewModel

@Composable
fun PostsScreen(vm: ApiViewModel) {
    val posts = vm.posts.collectAsState()

    LaunchedEffect(Unit) {
        vm.fetchPosts()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            "Posts de ejemplo",
            style = MaterialTheme.typography.titleLarge
        )
        Button(onClick = { vm.fetchPosts() }) {
            Text("Recargar")
        }
        posts.value.forEach { p ->
            Text("• #${p.id}  ${p.title}")
        }
    }
}
