package com.example.composegallery.feature.photodetail.domain.usecase

import com.example.composegallery.core.domain.model.DownloadStatus
import com.example.composegallery.core.domain.model.Photo
import com.example.composegallery.core.domain.repository.GalleryRepository
import com.example.composegallery.core.domain.repository.PhotoActionService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

/**
 * Use case for downloading a photo and reporting the download to Unsplash.
 */
class DownloadPhotoUseCase @Inject constructor(
    private val photoActionService: PhotoActionService,
    private val galleryRepository: GalleryRepository
) {
    operator fun invoke(photo: Photo): Flow<DownloadStatus> {
        val fileName = "Snap_${photo.id}.jpg"
        return photoActionService.downloadPhoto(photo.fullUrl, fileName)
            .onEach { status ->
                if (status is DownloadStatus.Success) {
                    photo.downloadLocationUrl?.let { url ->
                        galleryRepository.reportDownload(url)
                    }
                }
            }
    }
}
