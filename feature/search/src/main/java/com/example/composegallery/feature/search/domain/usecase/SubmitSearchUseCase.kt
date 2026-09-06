package com.example.composegallery.feature.search.domain.usecase

import com.example.composegallery.core.model.Result
import com.example.composegallery.feature.search.domain.repository.SearchRepository
import javax.inject.Inject

/**
 * Use case for submitting a search query.
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
