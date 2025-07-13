package com.example.composegallery.feature.gallery.domain.usecase

import com.example.composegallery.feature.gallery.data.repository.FakeSearchRepository
import com.example.composegallery.feature.gallery.data.util.Result
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class SubmitSearchUseCaseTest {

    private lateinit var fakeRepository: FakeSearchRepository
    private lateinit var useCase: SubmitSearchUseCase

    @Before
    fun setUp() {
        fakeRepository = FakeSearchRepository()
        useCase = SubmitSearchUseCase(fakeRepository)
    }

    @Test
    fun submitSearch_shouldReturnError_whenQueryIsBlank() = runTest {
        val result = useCase("   ")

        when (result) {
            is Result.Error -> assertThat(result.message).isEqualTo("Empty query")
            else -> error("Expected Result.Error but got $result")
        }
    }

    @Test
    fun submitSearch_shouldTrimQuery_andReturnSuccess() = runTest {
        val result = useCase("  hello world  ")

        when (result) {
            is Result.Success -> assertThat(result.data).isEqualTo("hello world")
            else -> error("Expected Result.Success but got $result")
        }

        val recentSearches = fakeRepository.getRecentSearches(5).first()
        assertThat(recentSearches.first().query).isEqualTo("hello world")
    }

    @Test
    fun submitSearch_shouldReturnError_whenRepositoryFails() = runTest {
        val failingRepository = object : FakeSearchRepository() {
            override suspend fun saveRecentSearch(query: String): Result<Unit> {
                return Result.Error("Simulated failure")
            }
        }
        val failingUseCase = SubmitSearchUseCase(failingRepository)

        val result = failingUseCase("fail case")

        when (result) {
            is Result.Error -> assertThat(result.message).isEqualTo("Simulated failure")
            else -> error("Expected Result.Error but got $result")
        }
    }
}
