package com.example.composegallery.feature.photodetail.domain.usecase

import com.example.composegallery.core.domain.model.DownloadStatus
import com.example.composegallery.core.domain.model.Photo
import com.example.composegallery.feature.photodetail.fakes.FakeGalleryRepository
import com.example.composegallery.feature.photodetail.fakes.FakePhotoActionService
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DownloadPhotoUseCaseTest {

    private val photoActionService = FakePhotoActionService()
    private val galleryRepository = FakeGalleryRepository()
    private lateinit var useCase: DownloadPhotoUseCase

    @Before
    fun setup() {
        useCase = DownloadPhotoUseCase(photoActionService, galleryRepository)
    }

    @Test
    fun invoke_success_emitsSuccessAndReportsDownload() = runTest {
        val photo = createFakePhoto("1")
        val successStatus = DownloadStatus.Success("/path")
        photoActionService.setDownloadStatusFlow(flowOf(successStatus))

        val results = useCase(photo).toList()

        assertThat(results).contains(successStatus)
        assertThat(galleryRepository.lastReportedDownloadUrl).isEqualTo("https://example.com/download")
    }

    @Test
    fun invoke_progress_emitsProgress() = runTest {
        val photo = createFakePhoto("1")
        val progressStatus = DownloadStatus.Progress(50)
        photoActionService.setDownloadStatusFlow(flowOf(progressStatus))

        val results = useCase(photo).toList()

        assertThat(results).contains(progressStatus)
        assertThat(galleryRepository.lastReportedDownloadUrl).isNull()
    }

    private fun createFakePhoto(id: String) = Photo(
        id = id,
        width = 100,
        height = 100,
        thumbUrl = "",
        smallUrl = "",
        regularUrl = "",
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
