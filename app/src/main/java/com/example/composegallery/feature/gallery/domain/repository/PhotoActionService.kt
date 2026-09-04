package com.example.composegallery.feature.gallery.domain.repository

import com.example.composegallery.feature.gallery.data.util.Result
import com.example.composegallery.feature.gallery.domain.model.DownloadStatus
import kotlinx.coroutines.flow.Flow

interface PhotoActionService {
    fun downloadPhoto(url: String, fileName: String): Flow<DownloadStatus>
    suspend fun setWallpaper(url: String): Result<Unit>
}
