package com.example.composegallery.core.repository

import com.example.composegallery.core.common.Result
import com.example.composegallery.core.model.DownloadStatus
import kotlinx.coroutines.flow.Flow

interface PhotoActionService {
    fun downloadPhoto(url: String, fileName: String): Flow<DownloadStatus>
    suspend fun setWallpaper(url: String): Result<Unit>
}
