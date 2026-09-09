package com.example.composegallery.feature.home.data.repository

import com.example.composegallery.core.domain.model.Photo
import com.example.composegallery.feature.home.data.DefaultFavoriteRepository
import com.example.composegallery.core.testing.FakeFavoritePhotoDao
import com.example.composegallery.core.database.local.home.entity.FavoritePhotoEntity
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DefaultFavoriteRepositoryTest {

    private val favoritePhotoDao = FakeFavoritePhotoDao()
    private lateinit var repository: DefaultFavoriteRepository

    @Before
    fun setup() {
        repository = DefaultFavoriteRepository(favoritePhotoDao)
    }

    @Test
    fun getFavorites_mapsEntitiesToDomainModels() = runTest {
        val entity = createFakeFavoriteEntity("1")
        favoritePhotoDao.insertFavorite(entity)

        val favorites = repository.getFavorites().first()

        assertThat(favorites).hasSize(1)
        assertThat(favorites[0].id).isEqualTo("1")
    }

    @Test
    fun addFavorite_callsDao() = runTest {
        val photo = createFakePhoto("1")

        repository.addFavorite(photo)

        assertThat(favoritePhotoDao.isFavoriteOneShot("1")).isTrue()
    }

    @Test
    fun removeFavorite_callsDao() = runTest {
        val entity = createFakeFavoriteEntity("1")
        favoritePhotoDao.insertFavorite(entity)
        
        repository.removeFavorite("1")

        assertThat(favoritePhotoDao.isFavoriteOneShot("1")).isFalse()
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
