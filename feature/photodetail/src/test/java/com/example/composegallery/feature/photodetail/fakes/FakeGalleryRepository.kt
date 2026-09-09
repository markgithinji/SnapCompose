package com.example.composegallery.feature.photodetail.fakes

import androidx.paging.PagingData
import com.example.composegallery.core.common.Result
import com.example.composegallery.core.domain.model.Photo
import com.example.composegallery.core.domain.model.Topic
import com.example.composegallery.core.domain.repository.GalleryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeGalleryRepository : GalleryRepository {

    private val photos = mutableMapOf<String, Photo>()
    var lastReportedDownloadUrl: String? = null
        private set

    fun setPhotos(photoList: List<Photo>) {
        photos.clear()
        photoList.forEach { photos[it.id] = it }
    }

    override fun getPagedPhotos(): Flow<PagingData<Photo>> = flowOf(PagingData.empty())

    override fun getTopicPagedPhotos(topicIdOrSlug: String): Flow<PagingData<Photo>> = flowOf(PagingData.empty())

    override suspend fun getTopics(): Result<List<Topic>> = Result.Success(emptyList())

    override suspend fun getPhoto(photoId: String): Result<Photo> {
        return photos[photoId]?.let { Result.Success(it) } ?: Result.Error("Photo not found")
    }

    override suspend fun reportDownload(downloadUrl: String) {
        lastReportedDownloadUrl = downloadUrl
    }

    override suspend fun syncPhotos(
        topicId: String,
        page: Int,
        pageSize: Int,
        isRefresh: Boolean
    ): Result<Boolean> = Result.Success(true)
}
