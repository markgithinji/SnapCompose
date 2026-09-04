package com.example.composegallery.feature.gallery.domain.model

sealed class DownloadStatus {
    object Idle : DownloadStatus()
    data class Progress(val percentage: Int) : DownloadStatus()
    data class Success(val path: String) : DownloadStatus()
    data class Error(val message: String) : DownloadStatus()
}
