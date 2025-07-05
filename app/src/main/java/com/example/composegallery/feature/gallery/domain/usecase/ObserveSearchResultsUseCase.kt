package com.example.composegallery.feature.gallery.domain.usecase

import androidx.paging.PagingData
import com.example.composegallery.feature.gallery.domain.model.Photo
import com.example.composegallery.feature.gallery.domain.repository.GalleryRepository
import com.example.composegallery.feature.gallery.domain.repository.SearchRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

/**
 * Use case for observing search results for photos.
 *
 * This use case takes a [StateFlow] of search queries as input and returns a [Flow]
 * of [PagingData] containing [Photo] objects that match the latest query.
 *
 * It incorporates several optimizations:
 * - **Debouncing:** It waits for a short period (300ms) after the user stops typing
 *   before actually performing the search. This prevents excessive API calls.
 * - **DistinctUntilChanged:** It only performs a new search if the query has actually
 *   changed since the last search.
 * - **Filter:** It ignores blank queries, preventing unnecessary searches.
 * - **FlatMapLatest:** It ensures that only the results for the latest query are emitted,
 *   cancelling any ongoing searches for previous queries.
 *
 * @param searchRepository The repository responsible for fetching search results.
 */
class ObserveSearchResultsUseCase @Inject constructor(
    private val searchRepository: SearchRepository
) {
    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    operator fun invoke(queryFlow: StateFlow<String>): Flow<PagingData<Photo>> {
        return queryFlow
            .debounce(300L)
            .distinctUntilChanged()
            .filter { it.isNotBlank() }
            .flatMapLatest { query ->
                searchRepository.searchPagedPhotos(query)
            }
    }
}