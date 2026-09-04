package com.example.composegallery.feature.gallery.domain.usecase

import com.example.composegallery.feature.gallery.domain.model.DownloadStatus
import com.example.composegallery.feature.gallery.domain.model.Photo
import com.example.composegallery.feature.gallery.domain.repository.GalleryRepository
import com.example.composegallery.feature.gallery.domain.repository.PhotoActionService
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

class DownloadPhotoUseCaseTest {

    private val photoActionService: PhotoActionService = mock()
    private val galleryRepository: GalleryRepository = mock()
    private lateinit var useCase: DownloadPhotoUseCase

    @Before
    fun setup() {
        useCase = DownloadPhotoUseCase(photoActionService, galleryRepository)
    }

    @Test
    fun invoke_success_emitsSuccessAndReportsDownload() = runTest {
        val photo = createFakePhoto("1")
        val successStatus = DownloadStatus.Success("/path")
        whenever(photoActionService.downloadPhoto(any(), any())).thenReturn(flowOf(successStatus))

        val results = useCase(photo).toList()

        assertThat(results).contains(successStatus)
        verify(galleryRepository).reportDownload("https://example.com/download")
    }

    @Test
    fun invoke_progress_emitsProgress() = runTest {
        val photo = createFakePhoto("1")
        val progressStatus = DownloadStatus.Progress(50)
        whenever(photoActionService.downloadPhoto(any(), any())).thenReturn(flowOf(progressStatus))

        val results = useCase(photo).toList()

        assertThat(results).contains(progressStatus)
        verify(galleryRepository, never()).reportDownload(any())
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
