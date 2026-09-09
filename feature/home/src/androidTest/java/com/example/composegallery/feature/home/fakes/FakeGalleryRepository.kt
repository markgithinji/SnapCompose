package com.example.composegallery.feature.home.fakes

import androidx.paging.PagingData
import com.example.composegallery.core.common.Result
import com.example.composegallery.core.domain.model.Photo
import com.example.composegallery.core.domain.model.Topic
import com.example.composegallery.core.domain.repository.GalleryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeGalleryRepository : GalleryRepository {
    private var syncResult: Result<Boolean> = Result.Success(true)

    fun setSyncResult(result: Result<Boolean>) {
        syncResult = result
    }

    override fun getPagedPhotos(): Flow<PagingData<Photo>> = flowOf(PagingData.empty())
    override fun getTopicPagedPhotos(topicIdOrSlug: String): Flow<PagingData<Photo>> = flowOf(PagingData.empty())
    override suspend fun getTopics(): Result<List<Topic>> = Result.Success(emptyList())
    override suspend fun getPhoto(photoId: String): Result<Photo> = Result.Error("Not implemented")
    override suspend fun reportDownload(downloadUrl: String) {}
    override suspend fun syncPhotos(topicId: String, page: Int, pageSize: Int, isRefresh: Boolean): Result<Boolean> = syncResult
}
