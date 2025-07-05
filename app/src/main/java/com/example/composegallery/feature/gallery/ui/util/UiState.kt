package com.example.composegallery.feature.gallery.ui.util

sealed interface UiState<out T> {
    object Loading : UiState<Nothing>
    data class Error(val message: String) : UiState<Nothing>
    data class Content<T>(val data: T) : UiState<T>
}
