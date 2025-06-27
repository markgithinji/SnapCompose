package com.example.composegallery.feature.gallery.domain.model

import androidx.paging.PagingData
import com.example.composegallery.feature.gallery.domain.repository.GalleryRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

class ObserveSearchResultsUseCase @Inject constructor(
    private val galleryRepository: GalleryRepository
) {
    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    operator fun invoke(queryFlow: StateFlow<String>): Flow<PagingData<Photo>> {
        return queryFlow
            .debounce(300L)
            .distinctUntilChanged()
            .filter { it.isNotBlank() }
            .flatMapLatest { query ->
                galleryRepository.searchPagedPhotos(query)
            }
    }
}