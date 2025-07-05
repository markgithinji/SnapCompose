package com.example.composegallery.feature.gallery.domain.usecase

import com.example.composegallery.feature.gallery.data.util.Result
import com.example.composegallery.feature.gallery.domain.repository.SearchRepository
import javax.inject.Inject

/**
 * Use case for submitting a search query.
 *
 * This use case handles the logic of validating and saving a search query.
 *
 * @property repository The [SearchRepository] responsible for data operations related to searches.
 */
class SubmitSearchUseCase @Inject constructor(
    private val repository: SearchRepository
) {
    suspend operator fun invoke(query: String): Result<String> {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return Result.Error("Empty query")

        return when (val result = repository.saveRecentSearch(trimmed)) {
            is Result.Success -> Result.Success(trimmed)
            is Result.Error -> Result.Error(result.message, result.throwable)
        }
    }
}

