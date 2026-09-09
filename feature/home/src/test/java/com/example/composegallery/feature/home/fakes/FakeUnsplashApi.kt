package com.example.composegallery.feature.home.fakes

import com.example.composegallery.core.network.model.SearchResponseDto
import com.example.composegallery.core.network.model.UnsplashCollectionDto
import com.example.composegallery.core.network.model.UnsplashPhotoDto
import com.example.composegallery.core.network.model.UnsplashTopicDto
import com.example.composegallery.core.network.model.UnsplashUserDto
import com.example.composegallery.core.network.model.UserStatisticsDto
import com.example.composegallery.core.network.remote.UnsplashApi

class FakeUnsplashApi : UnsplashApi {

    private var photoResult: UnsplashPhotoDto? = null
    private var photoException: Exception? = null

    fun setPhotoResult(photo: UnsplashPhotoDto?) {
        photoResult = photo
    }

    fun setPhotoException(exception: Exception) {
        photoException = exception
    }

    override suspend fun getPhotos(page: Int, perPage: Int): List<UnsplashPhotoDto> = emptyList()

    override suspend fun getTopics(page: Int, perPage: Int, orderBy: String): List<UnsplashTopicDto> = emptyList()

    override suspend fun getTopicPhotos(
        topicIdOrSlug: String,
        page: Int,
        perPage: Int
    ): List<UnsplashPhotoDto> = emptyList()

    override suspend fun searchPhotos(
        query: String,
        page: Int,
        perPage: Int,
        orientation: String?,
        color: String?,
        orderBy: String?
    ): SearchResponseDto = SearchResponseDto(0, 0, emptyList())

    override suspend fun getPhoto(photoId: String): UnsplashPhotoDto {
        photoException?.let { throw it }
        return photoResult ?: throw IllegalStateException("Photo result not set")
    }

    override suspend fun getUser(username: String): UnsplashUserDto = throw NotImplementedError()

    override suspend fun getUserPhotos(username: String, page: Int, perPage: Int): List<UnsplashPhotoDto> = emptyList()

    override suspend fun getUserCollections(username: String, page: Int, perPage: Int): List<UnsplashCollectionDto> = emptyList()

    override suspend fun getUserLikedPhotos(username: String, page: Int, perPage: Int): List<UnsplashPhotoDto> = emptyList()

    override suspend fun getCollectionPhotos(collectionId: String, page: Int, perPage: Int): List<UnsplashPhotoDto> = emptyList()

    override suspend fun getUserStatistics(username: String): UserStatisticsDto = throw NotImplementedError()

    override suspend fun triggerDownload(url: String) {
        // No-op
    }
}
