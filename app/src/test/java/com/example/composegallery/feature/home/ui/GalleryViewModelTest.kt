package com.example.composegallery.feature.home.ui

import com.example.composegallery.R
import com.example.composegallery.core.data.Result
import com.example.composegallery.core.model.DownloadStatus
import com.example.composegallery.core.model.Photo
import com.example.composegallery.core.util.UiState
import com.example.composegallery.core.util.StringProvider
import com.example.composegallery.feature.home.domain.repository.FavoriteRepository
import com.example.composegallery.feature.home.domain.repository.GalleryRepository
import com.example.composegallery.feature.photodetail.domain.usecase.DownloadPhotoUseCase
import com.example.composegallery.feature.photodetail.domain.usecase.SetWallpaperUseCase
import com.example.composegallery.feature.photodetail.domain.usecase.ToggleFavoriteUseCase
import com.example.composegallery.utils.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
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

        viewModel.downloadPhoto(photo)

        assertThat(viewModel.downloadStatus.value).isEqualTo(DownloadStatus.Success("/path"))
    }

    @Test
    fun setWallpaper_success_updatesWallpaperLoading() = runTest {
        val photo = createFakePhoto("1")
        whenever(setWallpaperUseCase(photo)).thenReturn(Result.Success(Unit))

        viewModel.setWallpaper(photo)

        assertThat(viewModel.isWallpaperLoading.value).isFalse()
    }

    @Test
    fun isFavorite_emitsCorrectValues() = runTest {
        val photoId = "1"
        val photo = createFakePhoto(photoId)
        val favoriteFlow = MutableStateFlow(false)
        whenever(favoriteRepository.isFavorite(photoId)).thenReturn(favoriteFlow)
        whenever(galleryRepository.getPhoto(photoId)).thenReturn(Result.Success(photo))

        val favorites = mutableListOf<Boolean>()
        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.isFavorite.collect { favorites.add(it) }
        }

        viewModel.loadPhoto(photoId)
        favoriteFlow.value = true

        assertThat(favorites).containsAtLeast(false, true).inOrder()
        job.cancel()
    }

    @Test
    fun selectTopic_triggersRepositoryCall() = runTest {
        val topicId = "nature"
        whenever(galleryRepository.getTopicPagedPhotos(topicId)).thenReturn(flowOf())

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.pagedPhotos.collect {}
        }

        viewModel.selectTopic(topicId)

        verify(galleryRepository).getTopicPagedPhotos(topicId)
        job.cancel()
    }

    @Test
    fun selectTopic_null_triggersEditorialCall() = runTest {
        whenever(galleryRepository.getPagedPhotos()).thenReturn(flowOf())

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.pagedPhotos.collect {}
        }

        viewModel.selectTopic(null)

        verify(galleryRepository).getPagedPhotos()
        job.cancel()
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
