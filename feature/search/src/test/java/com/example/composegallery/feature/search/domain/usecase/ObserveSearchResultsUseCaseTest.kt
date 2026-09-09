package com.example.composegallery.feature.search.domain.usecase

import androidx.recyclerview.widget.DiffUtil
import com.example.composegallery.core.domain.model.Photo
import com.example.composegallery.core.util.collectItemsForTest
import com.example.composegallery.core.testing.FakeSearchRepository
import com.example.composegallery.core.domain.model.SearchFilters
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
class ObserveSearchResultsUseCaseTest {

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
        fakeRepository = FakeSearchRepository().apply {
            addFakeResult(
                "cats", listOf(
                    createPhoto("1", "A cute cat"),
                    createPhoto("2", "Another cat")
                )
            )
            addFakeResult(
                "dogs", listOf(
                    createPhoto("3", "A happy dog")
                )
            )
        }
        useCase = ObserveSearchResultsUseCase(fakeRepository)
    }

    private fun createPhoto(id: String, description: String) = Photo(
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
        description = description
    )

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun observeSearchResults_shouldEmitPagingData_whenQueryIsValid() = runTest(testDispatcher) {
        val filtersFlow = MutableStateFlow(SearchFilters(query = "cats"))
        val resultFlow = useCase(filtersFlow)

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
            val filtersFlow = MutableStateFlow(SearchFilters(query = ""))
            val resultFlow = useCase(filtersFlow)

            advanceTimeBy(350)
            advanceUntilIdle()

            val snapshot = resultFlow.collectItemsForTest(diffCallback, testDispatcher)

            assertThat(snapshot).isEmpty()
        }

    @Test
    fun observeSearchResults_shouldEmitNewPagingData_forDifferentQueries() =
        runTest(testDispatcher) {
            val filtersFlow = MutableStateFlow(SearchFilters(query = "cats"))
            val catsFlow = useCase(filtersFlow)

            advanceTimeBy(350)
            advanceUntilIdle()
            val catsSnapshot = catsFlow.collectItemsForTest(diffCallback, testDispatcher)
            assertThat(catsSnapshot).hasSize(2)
            assertThat(catsSnapshot[0].description).isEqualTo("A cute cat")

            filtersFlow.value = SearchFilters(query = "dogs")
            val dogsFlow = useCase(filtersFlow)

            advanceTimeBy(350)
            advanceUntilIdle()
            val dogsSnapshot = dogsFlow.collectItemsForTest(diffCallback, testDispatcher)
            assertThat(dogsSnapshot).hasSize(1)
            assertThat(dogsSnapshot[0].description).isEqualTo("A happy dog")
        }

    @Test
    fun observeSearchResults_shouldDebounceRapidQueryChanges() = runTest(testDispatcher) {
        val filtersFlow = MutableStateFlow(SearchFilters(query = ""))

        filtersFlow.value = SearchFilters(query = "c")
        advanceTimeBy(100)
        filtersFlow.value = SearchFilters(query = "ca")
        advanceTimeBy(100)
        filtersFlow.value = SearchFilters(query = "cat")
        advanceTimeBy(100)
        filtersFlow.value = SearchFilters(query = "cats")

        val resultFlow = useCase(filtersFlow)
        advanceTimeBy(350)
        advanceUntilIdle()

        val snapshot = resultFlow.collectItemsForTest(diffCallback, testDispatcher)

        assertThat(snapshot).hasSize(2)
        assertThat(snapshot[0].description).isEqualTo("A cute cat")
    }
}
