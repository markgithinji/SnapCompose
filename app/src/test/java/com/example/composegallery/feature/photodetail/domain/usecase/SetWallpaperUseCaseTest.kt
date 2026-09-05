package com.example.composegallery.feature.photodetail.domain.usecase

import com.example.composegallery.core.data.Result
import com.example.composegallery.core.model.Photo
import com.example.composegallery.feature.home.domain.repository.GalleryRepository
import com.example.composegallery.feature.photodetail.domain.repository.PhotoActionService
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

class SetWallpaperUseCaseTest {

    private val photoActionService: PhotoActionService = mock()
    private val galleryRepository: GalleryRepository = mock()
    private lateinit var useCase: SetWallpaperUseCase

    @Before
    fun setup() {
        useCase = SetWallpaperUseCase(photoActionService, galleryRepository)
    }

    @Test
    fun invoke_success_returnsSuccessAndReportsDownload() = runTest {
        val photo = createFakePhoto("1")
        whenever(photoActionService.setWallpaper(photo.fullUrl)).thenReturn(Result.Success(Unit))

        val result = useCase(photo)

        assertThat(result).isInstanceOf(Result.Success::class.java)
        verify(galleryRepository).reportDownload("https://example.com/download")
    }

    @Test
    fun invoke_error_returnsErrorAndDoesNotReport() = runTest {
        val photo = createFakePhoto("1")
        whenever(photoActionService.setWallpaper(photo.fullUrl)).thenReturn(Result.Error("Failed"))

        val result = useCase(photo)

        assertThat(result).isInstanceOf(Result.Error::class.java)
        assertThat((result as Result.Error).message).isEqualTo("Failed")
        verify(galleryRepository, never()).reportDownload(any())
    }

    private fun createFakePhoto(id: String) = Photo(
        id = id,
        width = 100,
        height = 100,
        thumbUrl = "",
        smallUrl = "",
        regularUrl = "https://example.com/regular.jpg",
        fullUrl = "https://example.com/full.jpg",
        authorName = "Author",
        authorProfileImageUrl = "",
        authorProfileImageMediumResUrl = "",
        authorProfileImageHighResUrl = "",
        authorUnsplashUrl = "",
        username = "",
        downloadLocationUrl = "https://example.com/download",
        location = null,
        description = ""
    )
}
