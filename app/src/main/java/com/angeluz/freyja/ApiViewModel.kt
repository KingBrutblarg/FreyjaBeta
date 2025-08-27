package com.angeluz.freyja

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.angeluz.freyja.data.PostDto
import com.angeluz.freyja.data.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ApiViewModel : ViewModel() {
    private val repo = Repository()

    private val _posts = MutableStateFlow<List<PostDto>>(emptyList())
    val posts: StateFlow<List<PostDto>> = _posts

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadPosts() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _posts.value = repo.fetchPosts()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}