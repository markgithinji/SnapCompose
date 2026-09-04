package com.example.composegallery.feature.gallery.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PhotoDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: PhotoDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        dao = database.photoDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertAndGetPhoto() = runTest {
        val photo = createFakePhotoEntity("1", "topic1")
        dao.insertPhotos(listOf(photo))

        val result = dao.getPhotoById("1")

        assertThat(result).isNotNull()
        assertThat(result?.id).isEqualTo("1")
        assertThat(result?.topicId).isEqualTo("topic1")
    }

    @Test
    fun clearAll_removesPhotosForSpecificTopic() = runTest {
        val photo1 = createFakePhotoEntity("1", "topic1")
        val photo2 = createFakePhotoEntity("2", "topic2")
        dao.insertPhotos(listOf(photo1, photo2))

        dao.clearAll("topic1")

        assertThat(dao.getPhotoById("1")).isNull()
        assertThat(dao.getPhotoById("2")).isNotNull()
    }

    @Test
    fun getCount_returnsCorrectCountForTopic() = runTest {
        val photo1 = createFakePhotoEntity("1", "topic1")
        val photo2 = createFakePhotoEntity("2", "topic1")
        val photo3 = createFakePhotoEntity("3", "topic2")
        dao.insertPhotos(listOf(photo1, photo2, photo3))

        val count = dao.getCount("topic1")

        assertThat(count).isEqualTo(2)
    }

    private fun createFakePhotoEntity(id: String, topicId: String) = PhotoEntity(
        id = id,
        topicId = topicId,
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
        authorUnsplashUrl = null,
        username = null,
        downloadLocationUrl = null,
        location = null,
        pagingOrder = 0
    )
}
