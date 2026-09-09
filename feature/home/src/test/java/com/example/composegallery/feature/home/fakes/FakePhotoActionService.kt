package com.example.composegallery.feature.home.fakes

import com.example.composegallery.core.common.Result
import com.example.composegallery.core.domain.model.DownloadStatus
import com.example.composegallery.core.domain.repository.PhotoActionService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakePhotoActionService : PhotoActionService {

    private var downloadStatusFlow: Flow<DownloadStatus> = flowOf(DownloadStatus.Idle)
    private var wallpaperResult: Result<Unit> = Result.Success(Unit)
    private var shareResult: Result<String> = Result.Success("content://fake_uri")

    fun setDownloadStatusFlow(flow: Flow<DownloadStatus>) {
        downloadStatusFlow = flow
    }

    fun setWallpaperResult(result: Result<Unit>) {
        wallpaperResult = result
    }

    fun setShareResult(result: Result<String>) {
        shareResult = result
    }

    override fun downloadPhoto(url: String, fileName: String): Flow<DownloadStatus> = downloadStatusFlow

    override suspend fun setWallpaper(url: String): Result<Unit> = wallpaperResult

    override suspend fun getPhotoForSharing(url: String): Result<String> = shareResult
}
