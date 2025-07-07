package com.example.composegallery.feature.gallery.domain.usecase

import androidx.recyclerview.widget.DiffUtil
import collectItemsForTest
import com.example.composegallery.feature.gallery.data.repository.FakeSearchRepository
import com.example.composegallery.feature.gallery.domain.model.Photo
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveSearchResultsUseCaseTest { // TODO: Fix this test

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var useCase: ObserveSearchResultsUseCase
    private lateinit var fakeRepository: FakeSearchRepository

    private val diffCallback = object : DiffUtil.ItemCallback<Photo>() {
        override fun areItemsTheSame(oldItem: Photo, newItem: Photo) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Photo, newItem: Photo) = oldItem == newItem
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeSearchRepository()
        useCase = ObserveSearchResultsUseCase(fakeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun observeSearchResults_shouldEmitPagingData_whenQueryIsValid() = runTest(testDispatcher) {
        val queryFlow = MutableStateFlow("cats")
        val resultFlow = useCase(queryFlow)

        advanceTimeBy(350)
        advanceUntilIdle()

        val snapshot = resultFlow.collectItemsForTest(diffCallback, testDispatcher)

        assertThat(snapshot).hasSize(2)
        assertThat(snapshot[0].id).isEqualTo("1")
        assertThat(snapshot[0].description).isEqualTo("A cute cat")
        assertThat(snapshot[1].id).isEqualTo("2")
        assertThat(snapshot[1].description).isEqualTo("Another cat")
    }

    @Test
    fun observeSearchResults_shouldEmitEmptyPagingData_whenQueryIsBlank() =
        runTest(testDispatcher) {
            val queryFlow = MutableStateFlow("")
            val resultFlow = useCase(queryFlow)

            advanceTimeBy(350)
            advanceUntilIdle()

            val snapshot = resultFlow.collectItemsForTest(diffCallback, testDispatcher)

            assertThat(snapshot).isEmpty()
        }

    @Test
    fun observeSearchResults_shouldEmitNewPagingData_forDifferentQueries() =
        runTest(testDispatcher) {
            val queryFlow = MutableStateFlow("cats")
            val catsFlow = useCase(queryFlow)

            advanceTimeBy(350)
            advanceUntilIdle()
            val catsSnapshot = catsFlow.collectItemsForTest(diffCallback, testDispatcher)
            assertThat(catsSnapshot).hasSize(2)
            assertThat(catsSnapshot[0].description).isEqualTo("A cute cat")

            queryFlow.value = "dogs"
            val dogsFlow = useCase(queryFlow)

            advanceTimeBy(350)
            advanceUntilIdle()
            val dogsSnapshot = dogsFlow.collectItemsForTest(diffCallback, testDispatcher)
            assertThat(dogsSnapshot).hasSize(1)
            assertThat(dogsSnapshot[0].description).isEqualTo("A happy dog")
        }

    @Test
    fun observeSearchResults_shouldDebounceRapidQueryChanges() = runTest(testDispatcher) {
        val queryFlow = MutableStateFlow("")

        queryFlow.value = "c"
        advanceTimeBy(100)
        queryFlow.value = "ca"
        advanceTimeBy(100)
        queryFlow.value = "cat"
        advanceTimeBy(100)
        queryFlow.value = "cats"

        val resultFlow = useCase(queryFlow)
        advanceTimeBy(350)
        advanceUntilIdle()

        val snapshot = resultFlow.collectItemsForTest(diffCallback, testDispatcher)

        assertThat(snapshot).hasSize(2)
        assertThat(snapshot[0].description).isEqualTo("A cute cat")
    }
}
