package com.example.composegallery.feature.search.domain.model

import com.example.composegallery.core.domain.model.SearchFilters
import com.example.composegallery.core.domain.model.OrderBy
import com.example.composegallery.core.domain.model.Orientation
import com.example.composegallery.core.domain.model.ColorFilter
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class SearchFiltersTest {

    @Test
    fun searchFilters_defaultValues_areCorrect() {
        val filters = SearchFilters()

        assertThat(filters.query).isEmpty()
        assertThat(filters.orderBy).isEqualTo(OrderBy.RELEVANT)
        assertThat(filters.orientation).isNull()
        assertThat(filters.color).isNull()
    }

    @Test
    fun searchFilters_copy_updatesValues() {
        val filters = SearchFilters()
        val updated = filters.copy(query = "cats", orderBy = OrderBy.LATEST)

        assertThat(updated.query).isEqualTo("cats")
        assertThat(updated.orderBy).isEqualTo(OrderBy.LATEST)
    }
}
