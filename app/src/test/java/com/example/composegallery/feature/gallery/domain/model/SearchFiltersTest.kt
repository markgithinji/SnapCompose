package com.example.composegallery.feature.gallery.domain.model

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
