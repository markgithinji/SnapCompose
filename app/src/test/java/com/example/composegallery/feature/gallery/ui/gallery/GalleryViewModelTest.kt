package com.example.composegallery.feature.gallery.ui.gallery

import com.example.composegallery.R
import com.example.composegallery.feature.gallery.data.util.Result
import com.example.composegallery.feature.gallery.domain.model.DownloadStatus
import com.example.composegallery.feature.gallery.domain.model.Photo
import com.example.composegallery.feature.gallery.domain.repository.FavoriteRepository
import com.example.composegallery.feature.gallery.domain.repository.GalleryRepository
import com.example.composegallery.feature.gallery.domain.usecase.DownloadPhotoUseCase
import com.example.composegallery.feature.gallery.domain.usecase.SetWallpaperUseCase
import com.example.composegallery.feature.gallery.domain.usecase.ToggleFavoriteUseCase
import com.example.composegallery.feature.gallery.ui.util.UiState
import com.example.composegallery.feature.gallery.util.StringProvider
import com.example.composegallery.utils.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class GalleryViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val galleryRepository: GalleryRepository = mock()
    private val favoriteRepository: FavoriteRepository = mock()
    private val downloadPhotoUseCase: DownloadPhotoUseCase = mock()
    private val setWallpaperUseCase: SetWallpaperUseCase = mock()
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase = mock()
    private val stringProvider: StringProvider = mock()

    private lateinit var viewModel: GalleryViewModel

    @Before
    fun setup() = runBlocking {
        whenever(favoriteRepository.getFavorites()).thenReturn(flowOf(emptyList()))
        whenever(galleryRepository.getPagedPhotos()).thenReturn(flowOf())
        whenever(galleryRepository.getTopics()).thenReturn(Result.Success(emptyList()))
        
        viewModel = GalleryViewModel(
            galleryRepository,
            favoriteRepository,
            downloadPhotoUseCase,
            setWallpaperUseCase,
            toggleFavoriteUseCase,
            stringProvider
        )
    }

    @Test
    fun loadPhoto_success_updatesUiState() = runTest {
        val photoId = "1"
        val photo = createFakePhoto(photoId)
        whenever(galleryRepository.getPhoto(photoId)).thenReturn(Result.Success(photo))

        viewModel.loadPhoto(photoId)

        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(UiState.Content::class.java)
        assertThat((state as UiState.Content).data).isEqualTo(photo)
    }

    @Test
    fun loadPhoto_error_updatesUiStateWithError() = runTest {
        val photoId = "1"
        whenever(galleryRepository.getPhoto(photoId)).thenReturn(Result.Error("Failed"))

        viewModel.loadPhoto(photoId)

        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(UiState.Error::class.java)
        assertThat((state as UiState.Error).message).isEqualTo("Failed")
    }

    @Test
    fun downloadPhoto_success_updatesDownloadStatus() = runTest {
        val photo = createFakePhoto("1")
        whenever(downloadPhotoUseCase(photo)).thenReturn(flowOf(DownloadStatus.Success("/path")))
        whenever(stringProvider.get(R.string.download_started)).thenReturn("Started")

        viewModel.downloadPhoto(photo)

        assertThat(viewModel.downloadStatus.value).isEqualTo(DownloadStatus.Success("/path"))
    }

    @Test
    fun setWallpaper_success_updatesWallpaperLoading() = runTest {
        val photo = createFakePhoto("1")
        whenever(setWallpaperUseCase(photo)).thenReturn(Result.Success(Unit))
        whenever(stringProvider.get(R.string.wallpaper_set_success)).thenReturn("Success")

        viewModel.setWallpaper(photo)

        assertThat(viewModel.isWallpaperLoading.value).isFalse()
    }

    private fun createFakePhoto(id: String) = Photo(
        id = id,
        width = 100,
        height = 100,
        thumbUrl = "",
        smallUrl = "",
        regularUrl = "",
        fullUrl = "",
        authorName = "Author",
        authorProfileImageUrl = "",
        authorProfileImageMediumResUrl = "",
        authorProfileImageHighResUrl = "",
        authorUnsplashUrl = "",
        username = "",
        downloadLocationUrl = "",
        location = null,
        description = ""
    )
}
