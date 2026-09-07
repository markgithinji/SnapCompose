package com.example.composegallery.feature.search.domain.usecase

import androidx.paging.PagingData
import com.example.composegallery.core.domain.model.Photo
import com.example.composegallery.core.domain.model.SearchFilters
import com.example.composegallery.feature.search.domain.repository.SearchRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

/**
 * Use case for observing search results for photos.
 */
class ObserveSearchResultsUseCase @Inject constructor(
    private val searchRepository: SearchRepository
) {
    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    operator fun invoke(filtersFlow: StateFlow<SearchFilters>): Flow<PagingData<Photo>> {
        return filtersFlow
            .debounce { filters ->
                if (filters.query.isBlank()) 0L else SEARCH_DEBOUNCE_MILLIS
            }
            .distinctUntilChanged { old, new ->
                old.query.trim() == new.query.trim() &&
                    old.orientation == new.orientation &&
                    old.color == new.color &&
                    old.orderBy == new.orderBy
            }
            .flatMapLatest { filters ->
                if (filters.query.isBlank()) {
                    flowOf(PagingData.empty())
                } else {
                    searchRepository.searchPagedPhotos(filters)
                }
            }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_MILLIS = 600L
    }
}
