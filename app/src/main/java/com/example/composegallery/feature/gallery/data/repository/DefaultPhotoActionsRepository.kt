package com.example.composegallery.feature.gallery.data.repository

import android.app.DownloadManager
import android.app.WallpaperManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.composegallery.R
import com.example.composegallery.feature.gallery.data.util.AppException
import com.example.composegallery.feature.gallery.data.util.Result
import com.example.composegallery.feature.gallery.domain.repository.PhotoActionsRepository
import com.example.composegallery.feature.gallery.util.StringProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultPhotoActionsRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val stringProvider: StringProvider
) : PhotoActionsRepository {

    override suspend fun downloadPhoto(url: String, fileName: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val request = DownloadManager.Request(Uri.parse(url))
                .setTitle(fileName)
                .setDescription(stringProvider.get(R.string.download_started))
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.enqueue(request)
            Result.Success(Unit)
        } catch (e: Exception) {
            val message = stringProvider.get(R.string.download_error)
            Result.Error(message, AppException(message, e))
        }
    }

    override suspend fun setWallpaper(url: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val loader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(url)
                .allowHardware(false) // Required for toBitmap()
                .build()

            val result = loader.execute(request)
            if (result is SuccessResult) {
                val bitmap = result.drawable.toBitmap()
                val wallpaperManager = WallpaperManager.getInstance(context)
                wallpaperManager.setBitmap(bitmap)
                Result.Success(Unit)
            } else {
                val message = stringProvider.get(R.string.wallpaper_set_error)
                Result.Error(message, AppException(message))
            }
        } catch (e: Exception) {
            val message = stringProvider.get(R.string.wallpaper_set_error)
            Result.Error(message, AppException(message, e))
        }
    }
}
