package com.example.composegallery.feature.gallery.data.repository


import com.example.composegallery.feature.gallery.data.model.HistoricalDto
import com.example.composegallery.feature.gallery.data.model.ProfileImageDto
import com.example.composegallery.feature.gallery.data.model.StatValueDto
import com.example.composegallery.feature.gallery.data.model.StatsDto
import com.example.composegallery.feature.gallery.data.model.UnsplashUserDto
import com.example.composegallery.feature.gallery.data.model.UserStatisticsDto
import com.example.composegallery.feature.gallery.data.remote.UnsplashApi
import com.example.composegallery.feature.gallery.data.util.Result
import com.example.composegallery.feature.gallery.util.StringProvider
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class DefaultUserRepositoryTest {

    private lateinit var api: UnsplashApi
    private lateinit var stringProvider: StringProvider
    private lateinit var repository: DefaultUserRepository

    @Before
    fun setup() {
        api = mock()
        stringProvider = mock()
        repository = DefaultUserRepository(api, stringProvider)
    }

    @Test
    fun getUserProfile_shouldReturnSuccess_whenApiReturnsValidUser() = runTest {
        val dto = fakeUnsplashUserDto(username = "jane_doe")
        whenever(api.getUser("jane_doe")).thenReturn(dto)

        val result = repository.getUserProfile("jane_doe")

        assertThat(result).isInstanceOf(Result.Success::class.java)
        val user = (result as Result.Success).data
        assertThat(user.username).isEqualTo("jane_doe")
    }

    @Test
    fun getUserProfile_shouldReturnError_whenApiFails() = runTest {
        whenever(api.getUser("fail")).thenThrow(RuntimeException("network down"))
        whenever(stringProvider.get(any())).thenReturn("Network error")

        val result = repository.getUserProfile("fail")

        assertThat(result).isInstanceOf(Result.Error::class.java)
        assertThat((result as Result.Error).message.lowercase()).contains("network")
    }

    @Test
    fun getUserStatistics_shouldReturnSuccess_whenApiReturnsValidData() = runTest {
        val dto = fakeUserStatisticsDto(username = "jane_doe")
        whenever(api.getUserStatistics("jane_doe")).thenReturn(dto)

        val result = repository.getUserStatistics("jane_doe")

        assertThat(result).isInstanceOf(Result.Success::class.java)
        val stats = (result as Result.Success).data
        assertThat(stats.username).isEqualTo("jane_doe")
    }

    @Test
    fun getUserStatistics_shouldReturnError_whenApiFails() = runTest {
        whenever(api.getUserStatistics("fail")).thenThrow(RuntimeException("API crash"))
        whenever(stringProvider.get(any())).thenReturn("API failure")

        val result = repository.getUserStatistics("fail")

        assertThat(result).isInstanceOf(Result.Error::class.java)
        assertThat((result as Result.Error).message.lowercase()).contains("api failure")
    }


    // ---------------------------
    // Fakes
    // ---------------------------

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
