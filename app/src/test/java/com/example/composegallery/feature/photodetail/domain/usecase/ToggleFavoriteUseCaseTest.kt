package com.example.composegallery.feature.photodetail.domain.usecase

import com.example.composegallery.core.model.Photo
import com.example.composegallery.feature.home.domain.repository.FavoriteRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class ToggleFavoriteUseCaseTest {

    private val favoriteRepository: FavoriteRepository = mock()
    private lateinit var useCase: ToggleFavoriteUseCase

    @Before
    fun setup() {
        useCase = ToggleFavoriteUseCase(favoriteRepository)
    }

    @Test
    fun invoke_whenIsFavorite_removesFavorite() = runTest {
        val photo = createFakePhoto("1")
        whenever(favoriteRepository.isFavoriteOneShot("1")).thenReturn(true)

        useCase(photo)

        verify(favoriteRepository).removeFavorite("1")
    }

    @Test
    fun invoke_whenIsNotFavorite_addsFavorite() = runTest {
        val photo = createFakePhoto("1")
        whenever(favoriteRepository.isFavoriteOneShot("1")).thenReturn(false)

        useCase(photo)

        verify(favoriteRepository).addFavorite(photo)
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
