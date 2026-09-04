package com.example.composegallery.feature.gallery.data.repository

import com.example.composegallery.feature.gallery.data.local.FavoritePhotoDao
import com.example.composegallery.feature.gallery.data.local.FavoritePhotoEntity
import com.example.composegallery.feature.gallery.domain.model.Photo
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class DefaultFavoriteRepositoryTest {

    private val favoritePhotoDao: FavoritePhotoDao = mock()
    private lateinit var repository: DefaultFavoriteRepository

    @Before
    fun setup() {
        repository = DefaultFavoriteRepository(favoritePhotoDao)
    }

    @Test
    fun getFavorites_mapsEntitiesToDomainModels() = runTest {
        val entity = createFakeFavoriteEntity("1")
        whenever(favoritePhotoDao.getAllFavorites()).thenReturn(flowOf(listOf(entity)))

        val favorites = repository.getFavorites().first()

        assertThat(favorites).hasSize(1)
        assertThat(favorites[0].id).isEqualTo("1")
    }

    @Test
    fun addFavorite_callsDao() = runTest {
        val photo = createFakePhoto("1")

        repository.addFavorite(photo)

        verify(favoritePhotoDao).insertFavorite(any())
    }

    @Test
    fun removeFavorite_callsDao() = runTest {
        repository.removeFavorite("1")

        verify(favoritePhotoDao).deleteFavorite("1")
    }

    private fun createFakeFavoriteEntity(id: String) = FavoritePhotoEntity(
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
        favoritedAt = System.currentTimeMillis()
    )

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
