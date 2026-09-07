package com.example.composegallery.core.domain.repository

import com.example.composegallery.core.common.Result
import com.example.composegallery.core.domain.model.DownloadStatus
import kotlinx.coroutines.flow.Flow

interface PhotoActionService {
    fun downloadPhoto(url: String, fileName: String): Flow<DownloadStatus>
    suspend fun setWallpaper(url: String): Result<Unit>
}
