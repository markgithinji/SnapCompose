package com.example.composegallery.core.database.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavoritePhotoDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: FavoritePhotoDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        dao = database.favoritePhotoDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertAndGetFavorite() = runTest {
        val favorite = createFakeFavorite("1")
        dao.insertFavorite(favorite)

        val favorites = dao.getAllFavorites().first()

        assertThat(favorites).hasSize(1)
        assertThat(favorites[0].id).isEqualTo("1")
    }

    @Test
    fun deleteFavorite() = runTest {
        val favorite = createFakeFavorite("1")
        dao.insertFavorite(favorite)
        dao.deleteFavorite("1")

        val favorites = dao.getAllFavorites().first()

        assertThat(favorites).isEmpty()
    }

    @Test
    fun isFavorite_returnsTrue_whenExists() = runTest {
        val favorite = createFakeFavorite("1")
        dao.insertFavorite(favorite)

        val isFav = dao.isFavorite("1").first()

        assertThat(isFav).isTrue()
    }

    @Test
    fun isFavorite_returnsFalse_whenDoesNotExist() = runTest {
        val isFav = dao.isFavorite("1").first()

        assertThat(isFav).isFalse()
    }

    private fun createFakeFavorite(id: String) = FavoritePhotoEntity(
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
        blurHash = null,
        description = null,
        createdAt = null,
        exif = null,
        favoritedAt = System.currentTimeMillis()
    )
}
