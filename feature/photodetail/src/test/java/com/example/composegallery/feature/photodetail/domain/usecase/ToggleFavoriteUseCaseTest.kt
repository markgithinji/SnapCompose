package com.example.composegallery.feature.photodetail.domain.usecase

import com.example.composegallery.core.domain.model.Photo
import com.example.composegallery.core.testing.FakeFavoriteRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class ToggleFavoriteUseCaseTest {

    private val favoriteRepository = FakeFavoriteRepository()
    private lateinit var useCase: ToggleFavoriteUseCase

    @Before
    fun setup() {
        useCase = ToggleFavoriteUseCase(favoriteRepository)
    }

    @Test
    fun invoke_whenIsFavorite_removesFavorite() = runTest {
        val photo = createFakePhoto("1")
        favoriteRepository.addFavorite(photo)

        useCase(photo)

        assertThat(favoriteRepository.isFavoriteOneShot("1")).isFalse()
    }

    @Test
    fun invoke_whenIsNotFavorite_addsFavorite() = runTest {
        val photo = createFakePhoto("1")

        useCase(photo)

        assertThat(favoriteRepository.isFavoriteOneShot("1")).isTrue()
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
