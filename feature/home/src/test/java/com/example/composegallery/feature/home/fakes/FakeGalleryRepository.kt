package com.example.composegallery.feature.home.fakes

import androidx.paging.PagingData
import com.example.composegallery.core.common.Result
import com.example.composegallery.core.domain.model.Photo
import com.example.composegallery.core.domain.model.Topic
import com.example.composegallery.core.domain.repository.GalleryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

class FakeGalleryRepository : GalleryRepository {

    private val photos = mutableMapOf<String, Photo>()
    private val topics = mutableListOf<Topic>()
    private var pagedPhotosFlow = flowOf<PagingData<Photo>>()
    private var topicPagedPhotosFlow = flowOf<PagingData<Photo>>()

    fun setPhotos(photoList: List<Photo>) {
        photos.clear()
        photoList.forEach { photos[it.id] = it }
    }

    fun setTopics(topicList: List<Topic>) {
        topics.clear()
        topics.addAll(topicList)
    }

    fun setPagedPhotos(flow: Flow<PagingData<Photo>>) {
        pagedPhotosFlow = flow
    }

    override fun getPagedPhotos(): Flow<PagingData<Photo>> = pagedPhotosFlow

    override fun getTopicPagedPhotos(topicIdOrSlug: String): Flow<PagingData<Photo>> = topicPagedPhotosFlow

    override suspend fun getTopics(): Result<List<Topic>> = Result.Success(topics)

    override suspend fun getPhoto(photoId: String): Result<Photo> {
        return photos[photoId]?.let { Result.Success(it) } ?: Result.Error("Photo not found")
    }

    override suspend fun reportDownload(downloadUrl: String) {
        // No-op
    }

    override suspend fun syncPhotos(
        topicId: String,
        page: Int,
        pageSize: Int,
        isRefresh: Boolean
    ): Result<Boolean> = Result.Success(true)
}
