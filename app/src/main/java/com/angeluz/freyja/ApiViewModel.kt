package com.angeluz.freyja

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.angeluz.freyja.data.PostDto
import com.angeluz.freyja.data.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ApiViewModel(
    private val repo: Repository = Repository()
) : ViewModel() {

    private val _posts = MutableStateFlow<List<PostDto>>(emptyList())
    val posts: StateFlow<List<PostDto>> = _posts

    fun fetchPosts() {
        viewModelScope.launch {
            val data = repo.fetchPosts()
            _posts.value = data
        }
    }
}
