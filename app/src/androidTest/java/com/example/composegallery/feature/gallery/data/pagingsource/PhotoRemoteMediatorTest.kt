package com.example.composegallery.feature.gallery.data.pagingsource

import androidx.paging.*
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.composegallery.feature.gallery.data.local.*
import com.example.composegallery.feature.gallery.data.model.*
import com.example.composegallery.feature.gallery.data.remote.UnsplashApi
import com.example.composegallery.feature.gallery.util.StringProvider
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.*

@OptIn(ExperimentalPagingApi::class)
@RunWith(AndroidJUnit4::class)
class PhotoRemoteMediatorTest {

    private lateinit var database: AppDatabase
    private val api: UnsplashApi = mock()
    private val stringProvider: StringProvider = mock()

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun refreshLoadReturnsSuccessResultWhenMoreDataIsPresent() = runTest {
        val topicId = "nature"
        val fakePhotos = listOf(
            createFakePhotoDto("1"),
            createFakePhotoDto("2")
        )
        whenever(api.getTopicPhotos(any(), any(), any())).thenReturn(fakePhotos)

        val mediator = PhotoRemoteMediator(api, database, stringProvider, topicId)
        val pagingState = PagingState<Int, PhotoEntity>(
            listOf(),
            null,
            PagingConfig(10),
            10
        )

        val result = mediator.load(LoadType.REFRESH, pagingState)

        assertThat(result is RemoteMediator.MediatorResult.Success).isTrue()
        assertThat((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached).isFalse()
        
        val photoCount = database.photoDao().getCount(topicId)
        assertThat(photoCount).isEqualTo(2)
    }

    @Test
    fun refreshLoadReturnsSuccessAndEndOfPaginationWhenNoMoreData() = runTest {
        val topicId = "nature"
        whenever(api.getTopicPhotos(any(), any(), any())).thenReturn(emptyList<UnsplashPhotoDto>())

        val mediator = PhotoRemoteMediator(api, database, stringProvider, topicId)
        val pagingState = PagingState<Int, PhotoEntity>(
            listOf(),
            null,
            PagingConfig(10),
            10
        )

        val result = mediator.load(LoadType.REFRESH, pagingState)

        assertThat(result is RemoteMediator.MediatorResult.Success).isTrue()
        assertThat((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached).isTrue()
    }

    @Test
    fun refreshLoadReturnsErrorResultWhenErrorOccurs() = runTest {
        val topicId = "nature"
        whenever(api.getTopicPhotos(any(), any(), any())).thenThrow(RuntimeException())
        whenever(stringProvider.get(any())).thenReturn("Error")

        val mediator = PhotoRemoteMediator(api, database, stringProvider, topicId)
        val pagingState = PagingState<Int, PhotoEntity>(
            listOf(),
            null,
            PagingConfig(10),
            10
        )

        val result = mediator.load(LoadType.REFRESH, pagingState)

        assertThat(result is RemoteMediator.MediatorResult.Error).isTrue()
    }

    @Test
    fun appendLoadReturnsSuccessResult() = runTest {
        val topicId = "nature"
        // 1. Setup initial data and keys
        val photo1 = createFakePhotoDto("1")
        val domain1 = photo1.toDomainModel()!!
        val entity1 = domain1.toEntity(topicId, 0)
        database.photoDao().insertPhotos(listOf(entity1))
        database.photoRemoteKeyDao().insertAll(listOf(
            PhotoRemoteKeyEntity("1", topicId, null, 2)
        ))

        // 2. Mock API for page 2
        val photo2 = createFakePhotoDto("2")
        whenever(api.getTopicPhotos(eq(topicId), eq(2), any())).thenReturn(listOf(photo2))

        val mediator = PhotoRemoteMediator(api, database, stringProvider, topicId)
        
        // PagingState needs the item
        val pagingState = PagingState<Int, PhotoEntity>(
            listOf(PagingSource.LoadResult.Page(listOf(entity1), null, 2)),
            null,
            PagingConfig(10),
            10
        )

        val result = mediator.load(LoadType.APPEND, pagingState)

        assertThat(result is RemoteMediator.MediatorResult.Success).isTrue()
        assertThat((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached).isFalse()
        
        val photoCount = database.photoDao().getCount(topicId)
        assertThat(photoCount).isEqualTo(2)
    }

    private fun createFakePhotoDto(id: String) = UnsplashPhotoDto(
        id = id,
        width = 100,
        height = 100,
        urls = UrlsDto(
            thumb = "thumb",
            small = "small",
            regular = "regular",
            full = "full"
        ),
        user = UserDto(
            username = "user",
            name = "Author",
            profileImage = ProfileImageDto(
                small = "s",
                medium = "m",
                large = "l"
            ),
            links = null,
            location = null
        ),
        likes = 0,
        blurHash = null,
        description = null,
        altDescription = null,
        createdAt = null,
        exif = null
    )
}
