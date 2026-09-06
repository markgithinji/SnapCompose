package com.example.composegallery.feature.photodetail.data

import android.app.WallpaperManager
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.composegallery.core.ui.R
import com.example.composegallery.core.data.AppException
import com.example.composegallery.core.model.Result
import com.example.composegallery.core.model.DownloadStatus
import com.example.composegallery.core.repository.PhotoActionService
import com.example.composegallery.core.util.StringProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject

class DefaultPhotoActionService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val stringProvider: StringProvider
) : PhotoActionService {

    private val client = OkHttpClient()

    override fun downloadPhoto(url: String, fileName: String): Flow<DownloadStatus> = flow {
        try {
            emit(DownloadStatus.Progress(0))
            
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            
            if (!response.isSuccessful) {
                emit(DownloadStatus.Error(stringProvider.get(R.string.download_error)))
                return@flow
            }
            
            val body = response.body ?: throw AppException("Empty response body")
            val totalBytes = body.contentLength()
            
            val contentResolver = context.contentResolver
            val imageCollection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }
            
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Snap")
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }
            }
            
            val imageUri = contentResolver.insert(imageCollection, contentValues) 
                ?: throw AppException("Failed to create new MediaStore entry")
            
            var bytesCopied: Long = 0
            val buffer = ByteArray(8 * 1024)
            var bytes: Int
            
            body.byteStream().use { input ->
                contentResolver.openOutputStream(imageUri).use { output ->
                    if (output == null) throw AppException("Failed to open output stream")
                    
                    bytes = input.read(buffer)
                    while (bytes != -1) {
                        output.write(buffer, 0, bytes)
                        bytesCopied += bytes
                        
                        if (totalBytes > 0) {
                            val progress = ((bytesCopied * 100) / totalBytes).toInt()
                            emit(DownloadStatus.Progress(progress))
                        }
                        
                        bytes = input.read(buffer)
                    }
                }
            }
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                contentResolver.update(imageUri, contentValues, null, null)
            }
            
            emit(DownloadStatus.Success(Environment.DIRECTORY_PICTURES + "/Snap/" + fileName))
        } catch (e: Exception) {
            emit(DownloadStatus.Error(e.localizedMessage ?: stringProvider.get(R.string.download_error)))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun setWallpaper(url: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val loader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(url)
                .allowHardware(false)
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
