package com.example.composegallery.feature.gallery.ui.profile

import com.example.composegallery.feature.gallery.data.util.Result
import com.example.composegallery.feature.gallery.domain.model.UnsplashUser
import com.example.composegallery.feature.gallery.domain.repository.UserRepository
import com.example.composegallery.feature.gallery.ui.util.UiState
import com.example.composegallery.utils.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class UserProfileViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val userRepository: UserRepository = mock()
    private lateinit var viewModel: UserProfileViewModel

    @Before
    fun setup() {
        viewModel = UserProfileViewModel(userRepository)
    }

    @Test
    fun setUsername_success_updatesUserProfileState() = runTest {
        val username = "janesmith"
        val user = UnsplashUser(
            id = "1",
            name = "Jane Smith",
            username = username,
            bio = "Photographer",
            location = "London",
            profileImageSmall = "",
            profileImageMedium = "",
            profileImageLarge = "",
            totalPhotos = 10,
            totalCollections = 2,
            totalLikes = 5,
            portfolioUrl = null,
            instagramUsername = null,
            unsplashProfileUrl = ""
        )
        whenever(userRepository.getUserProfile(username)).thenReturn(Result.Success(user))

        val states = mutableListOf<UiState<UnsplashUser>>()
        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.userProfileState.collect { states.add(it) }
        }

        viewModel.setUsername(username)

        assertThat(states.last()).isInstanceOf(UiState.Content::class.java)
        assertThat((states.last() as UiState.Content).data).isEqualTo(user)
        job.cancel()
    }

    @Test
    fun setUsername_error_updatesUserProfileStateWithError() = runTest {
        val username = "erroruser"
        whenever(userRepository.getUserProfile(username)).thenReturn(Result.Error("Not Found"))

        val states = mutableListOf<UiState<UnsplashUser>>()
        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.userProfileState.collect { states.add(it) }
        }

        viewModel.setUsername(username)

        assertThat(states.last()).isInstanceOf(UiState.Error::class.java)
        assertThat((states.last() as UiState.Error).message).isEqualTo("Not Found")
        job.cancel()
    }
}
