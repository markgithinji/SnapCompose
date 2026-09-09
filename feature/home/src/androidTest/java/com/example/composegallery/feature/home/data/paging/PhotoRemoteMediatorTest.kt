package com.example.composegallery.feature.home.data.paging

import androidx.paging.*
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.composegallery.core.database.local.AppDatabase
import com.example.composegallery.core.database.local.home.entity.PhotoEntity
import com.example.composegallery.feature.home.data.PhotoRemoteMediator
import com.example.composegallery.core.testing.FakeGalleryRepository
import com.example.composegallery.core.common.Result
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalPagingApi::class)
@RunWith(AndroidJUnit4::class)
class PhotoRemoteMediatorTest {

    private lateinit var database: AppDatabase
    private val galleryRepository = FakeGalleryRepository()

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
        galleryRepository.setSyncResult(Result.Success(false)) // More data present

        val mediator = PhotoRemoteMediator(database, galleryRepository, topicId)
        val pagingState = PagingState<Int, PhotoEntity>(
            listOf(),
            null,
            PagingConfig(10),
            10
        )

        val result = mediator.load(LoadType.REFRESH, pagingState)

        assertThat(result is RemoteMediator.MediatorResult.Success).isTrue()
        assertThat((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached).isFalse()
    }

    @Test
    fun refreshLoadReturnsSuccessAndEndOfPaginationWhenNoMoreData() = runTest {
        val topicId = "nature"
        galleryRepository.setSyncResult(Result.Success(true)) // End of pagination

        val mediator = PhotoRemoteMediator(database, galleryRepository, topicId)
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
        galleryRepository.setSyncResult(Result.Error("Error"))

        val mediator = PhotoRemoteMediator(database, galleryRepository, topicId)
        val pagingState = PagingState<Int, PhotoEntity>(
            listOf(),
            null,
            PagingConfig(10),
            10
        )

        val result = mediator.load(LoadType.REFRESH, pagingState)

        assertThat(result is RemoteMediator.MediatorResult.Error).isTrue()
    }
}
