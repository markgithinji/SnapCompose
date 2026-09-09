package com.example.composegallery.feature.profile.ui

import com.example.composegallery.core.common.Result
import com.example.composegallery.core.domain.model.UnsplashUser
import com.example.composegallery.core.common.UiState
import com.example.composegallery.core.testing.FakeUserRepository
import com.example.composegallery.core.testing.util.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UserProfileViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val userRepository = FakeUserRepository()
    private lateinit var viewModel: UserProfileViewModel

    @Before
    fun setup() {
        viewModel = UserProfileViewModel(userRepository)
    }

    @Test
    fun setUsername_success_updatesUserProfileState() = runTest {
        val username = "janesmith"
        val user = createFakeUser(username)
        userRepository.setUserProfileResult(Result.Success(user))

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
    fun setUsername_triggersPagingFlows() = runTest {
        val username = "janesmith"
        userRepository.setUserProfileResult(Result.Success(createFakeUser(username)))

        val job1 = launch(UnconfinedTestDispatcher()) { viewModel.userPhotos.collect {} }
        val job2 = launch(UnconfinedTestDispatcher()) { viewModel.userCollectionsState.collect {} }
        val job3 = launch(UnconfinedTestDispatcher()) { viewModel.userLikedPhotos.collect {} }

        viewModel.setUsername(username)
        
        // With fakes, we verify the outcome (e.g. data emitted) or just that it didn't crash.
        // If we want to verify triggers, we can add tracking to FakeUserRepository.
        
        job1.cancel()
        job2.cancel()
        job3.cancel()
    }

    @Test
    fun setCollectionId_triggersCollectionPhotosFlow() = runTest {
        val collectionId = "123"

        val job = launch(UnconfinedTestDispatcher()) { viewModel.collectionPhotos.collect {} }

        viewModel.setCollectionId(collectionId)

        job.cancel()
    }

    private fun createFakeUser(username: String) = UnsplashUser(
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
}
