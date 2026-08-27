package com.example.composegallery.feature.gallery.domain.usecase

import com.example.composegallery.feature.gallery.data.util.Result
import com.example.composegallery.feature.gallery.domain.model.Photo
import com.example.composegallery.feature.gallery.domain.repository.GalleryRepository
import com.example.composegallery.feature.gallery.domain.repository.PhotoActionService
import javax.inject.Inject

/**
 * Use case for setting a photo as device wallpaper and reporting the download to Unsplash.
 */
class SetWallpaperUseCase @Inject constructor(
    private val photoActionService: PhotoActionService,
    private val galleryRepository: GalleryRepository
) {
    suspend operator fun invoke(photo: Photo): Result<Unit> {
        val result = photoActionService.setWallpaper(photo.fullUrl)
        
        if (result is Result.Success) {
            photo.downloadLocationUrl?.let { url ->
                galleryRepository.reportDownload(url)
            }
        }
        
        return result
    }
}
