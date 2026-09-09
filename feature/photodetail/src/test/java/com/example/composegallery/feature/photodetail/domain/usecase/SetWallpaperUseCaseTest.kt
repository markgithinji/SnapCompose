package com.example.composegallery.feature.photodetail.domain.usecase

import com.example.composegallery.core.common.Result
import com.example.composegallery.core.domain.model.Photo
import com.example.composegallery.feature.photodetail.fakes.FakeGalleryRepository
import com.example.composegallery.feature.photodetail.fakes.FakePhotoActionService
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class SetWallpaperUseCaseTest {

    private val photoActionService = FakePhotoActionService()
    private val galleryRepository = FakeGalleryRepository()
    private lateinit var useCase: SetWallpaperUseCase

    @Before
    fun setup() {
        useCase = SetWallpaperUseCase(photoActionService, galleryRepository)
    }

    @Test
    fun invoke_success_returnsSuccessAndReportsDownload() = runTest {
        val photo = createFakePhoto("1")
        photoActionService.setWallpaperResult(Result.Success(Unit))

        val result = useCase(photo)

        assertThat(result).isInstanceOf(Result.Success::class.java)
        assertThat(galleryRepository.lastReportedDownloadUrl).isEqualTo("https://example.com/download")
    }

    @Test
    fun invoke_error_returnsErrorAndDoesNotReport() = runTest {
        val photo = createFakePhoto("1")
        photoActionService.setWallpaperResult(Result.Error("Failed"))

        val result = useCase(photo)

        assertThat(result).isInstanceOf(Result.Error::class.java)
        assertThat((result as Result.Error).message).isEqualTo("Failed")
        assertThat(galleryRepository.lastReportedDownloadUrl).isNull()
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
