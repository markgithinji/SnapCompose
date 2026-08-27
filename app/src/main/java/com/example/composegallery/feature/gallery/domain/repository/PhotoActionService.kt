package com.example.composegallery.feature.gallery.domain.repository

import com.example.composegallery.feature.gallery.data.util.Result

interface PhotoActionService {
    suspend fun downloadPhoto(url: String, fileName: String): Result<Unit>
    suspend fun setWallpaper(url: String): Result<Unit>
}
