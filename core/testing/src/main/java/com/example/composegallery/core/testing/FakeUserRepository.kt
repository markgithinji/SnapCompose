package com.example.composegallery.core.testing

import androidx.paging.PagingData
import com.example.composegallery.core.common.Result
import com.example.composegallery.core.domain.model.Photo
import com.example.composegallery.core.domain.model.PhotoCollection
import com.example.composegallery.core.domain.model.UnsplashUser
import com.example.composegallery.core.domain.model.UserStatistics
import com.example.composegallery.core.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeUserRepository : UserRepository {

    private var userProfileResult: Result<UnsplashUser> = Result.Error("Not set")
    private var userStatisticsResult: Result<UserStatistics> = Result.Error("Not set")

    fun setUserProfileResult(result: Result<UnsplashUser>) {
        userProfileResult = result
    }

    override suspend fun getUserProfile(username: String): Result<UnsplashUser> = userProfileResult

    override suspend fun getUserStatistics(username: String): Result<UserStatistics> = userStatisticsResult

    override fun getUserPhotos(username: String): Flow<PagingData<Photo>> = flowOf(PagingData.empty())

    override fun getUserCollections(username: String): Flow<PagingData<PhotoCollection>> = flowOf(PagingData.empty())

    override fun getUserLikedPhotos(username: String): Flow<PagingData<Photo>> = flowOf(PagingData.empty())

    override fun getCollectionPhotos(collectionId: String): Flow<PagingData<Photo>> = flowOf(PagingData.empty())
}
