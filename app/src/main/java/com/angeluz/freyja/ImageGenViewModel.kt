package com.angeluz.freyja

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.angeluz.freyja.data.ImageGenRequest
import com.angeluz.freyja.data.ImagesProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Base64
import android.graphics.BitmapFactory
import android.graphics.Bitmap

class ImageGenViewModel : ViewModel() {
    private val _url = MutableStateFlow<String?>(null)
    val url: StateFlow<String?> = _url

    private val _bitmap = MutableStateFlow<Bitmap?>(null)
    val bitmap: StateFlow<Bitmap?> = _bitmap

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun generate(prompt: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            _url.value = null
            _bitmap.value = null
            try {
                val res = ImagesProvider.api.generate(
                    ImageGenRequest(prompt = prompt, key = BuildConfig.IMG_API_KEY)
                )
                when {
                    !res.url.isNullOrBlank() -> _url.value = res.url
                    !res.base64.isNullOrBlank() -> {
                        val bytes = Base64.decode(res.base64, Base64.DEFAULT)
                        _bitmap.value = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    }
                    else -> _error.value = "Respuesta vacía del servidor"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error generando imagen"
            } finally {
                _loading.value = false
            }
        }
    }
}
