package com.example.composegallery.feature.gallery.ui.profile

import com.example.composegallery.feature.gallery.data.util.Result
import com.example.composegallery.feature.gallery.domain.model.UnsplashUser
import com.example.composegallery.feature.gallery.domain.repository.UserRepository
import com.example.composegallery.feature.gallery.ui.util.UiState
import com.example.composegallery.utils.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
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
        val user = createFakeUser(username)
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
    fun setUsername_triggersPagingFlows() = runTest {
        val username = "janesmith"
        whenever(userRepository.getUserPhotos(username)).thenReturn(flowOf())
        whenever(userRepository.getUserCollections(username)).thenReturn(flowOf())
        whenever(userRepository.getUserLikedPhotos(username)).thenReturn(flowOf())
        whenever(userRepository.getUserProfile(username)).thenReturn(Result.Success(createFakeUser(username)))

        // Collect flows to trigger them
        val job1 = launch(UnconfinedTestDispatcher()) { viewModel.userPhotos.collect {} }
        val job2 = launch(UnconfinedTestDispatcher()) { viewModel.userCollectionsState.collect {} }
        val job3 = launch(UnconfinedTestDispatcher()) { viewModel.userLikedPhotos.collect {} }

        viewModel.setUsername(username)

        verify(userRepository).getUserPhotos(username)
        verify(userRepository).getUserCollections(username)
        verify(userRepository).getUserLikedPhotos(username)
        
        job1.cancel()
        job2.cancel()
        job3.cancel()
    }

    @Test
    fun setCollectionId_triggersCollectionPhotosFlow() = runTest {
        val collectionId = "123"
        whenever(userRepository.getCollectionPhotos(collectionId)).thenReturn(flowOf())

        val job = launch(UnconfinedTestDispatcher()) { viewModel.collectionPhotos.collect {} }

        viewModel.setCollectionId(collectionId)

        verify(userRepository).getCollectionPhotos(collectionId)
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
