package com.example.composegallery.feature.profile.data.repository

import com.example.composegallery.core.common.Result
import com.example.composegallery.core.domain.model.UnsplashUser
import com.example.composegallery.core.domain.model.UserStatistics
import com.example.composegallery.core.network.model.HistoricalDto
import com.example.composegallery.core.network.model.ProfileImageDto
import com.example.composegallery.core.network.model.StatValueDto
import com.example.composegallery.core.network.model.StatsDto
import com.example.composegallery.core.network.model.UnsplashUserDto
import com.example.composegallery.core.network.model.UserStatisticsDto
import com.example.composegallery.feature.profile.data.DefaultUserRepository
import com.example.composegallery.core.testing.FakeStringProvider
import com.example.composegallery.core.testing.FakeUnsplashApi
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DefaultUserRepositoryTest {

    private val api = FakeUnsplashApi()
    private val stringProvider = FakeStringProvider()
    private lateinit var repository: DefaultUserRepository

    @Before
    fun setup() {
        repository = DefaultUserRepository(api, stringProvider)
    }

    @Test
    fun getUserProfile_shouldReturnSuccess_whenApiReturnsValidUser() = runTest {
        val dto = fakeUnsplashUserDto(username = "jane_doe")
        api.setUserResult(dto)

        val result = repository.getUserProfile("jane_doe")

        assertThat(result).isInstanceOf(Result.Success::class.java)
        val user = (result as Result.Success<*>).data as UnsplashUser
        assertThat(user.username).isEqualTo("jane_doe")
    }

    @Test
    fun getUserProfile_shouldReturnError_whenApiFails() = runTest {
        api.setException(RuntimeException("network down"))

        val result = repository.getUserProfile("fail")

        assertThat(result).isInstanceOf(Result.Error::class.java)
        assertThat((result as Result.Error).message.lowercase()).contains("fake")
    }

    @Test
    fun getUserStatistics_shouldReturnSuccess_whenApiReturnsValidData() = runTest {
        val dto = fakeUserStatisticsDto(username = "jane_doe")
        api.setUserStatisticsResult(dto)

        val result = repository.getUserStatistics("jane_doe")

        assertThat(result).isInstanceOf(Result.Success::class.java)
        val stats = (result as Result.Success<*>).data as UserStatistics
        assertThat(stats.username).isEqualTo("jane_doe")
    }

    @Test
    fun getUserStatistics_shouldReturnError_whenApiFails() = runTest {
        api.setException(RuntimeException("API crash"))

        val result = repository.getUserStatistics("fail")

        assertThat(result).isInstanceOf(Result.Error::class.java)
    }

    private fun fakeUnsplashUserDto(username: String): UnsplashUserDto {
        return UnsplashUserDto(
            id = "id_$username",
            username = username,
            name = "Jane Doe",
            bio = "Photographer",
            location = "Nairobi, Kenya",
            portfolioUrl = "https://janedoe.com",
            profileImage = ProfileImageDto(
                small = "https://example.com/small.jpg",
                medium = "https://example.com/medium.jpg",
                large = "https://example.com/large.jpg"
            ),
            instagramUsername = "janedoephoto",
            totalPhotos = 100,
            totalLikes = 2500,
            totalCollections = 12
        )
    }

    private fun fakeUserStatisticsDto(username: String): UserStatisticsDto {
        return UserStatisticsDto(
            username = username,
            downloads = StatsDto(
                total = 1000,
                historical = HistoricalDto(
                    change = 100,
                    average = 50,
                    resolution = "days",
                    quantity = 7,
                    values = listOf(
                        StatValueDto(date = "2024-07-01", value = 100),
                        StatValueDto(date = "2024-07-02", value = 120)
                    )
                )
            ),
            views = StatsDto(
                total = 2000,
                historical = HistoricalDto(
                    change = 200,
                    average = 100,
                    resolution = "days",
                    quantity = 7,
                    values = listOf(
                        StatValueDto(date = "2024-07-01", value = 200),
                        StatValueDto(date = "2024-07-02", value = 220)
                    )
                )
            )
        )
    }
}
