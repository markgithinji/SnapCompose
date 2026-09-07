package com.example.composegallery.core.repository

import androidx.paging.PagingData
import com.example.composegallery.core.model.Photo
import com.example.composegallery.core.common.Result
import com.example.composegallery.core.model.Topic
import kotlinx.coroutines.flow.Flow

interface GalleryRepository {
    fun getPagedPhotos(): Flow<PagingData<Photo>>
    fun getTopicPagedPhotos(topicIdOrSlug: String): Flow<PagingData<Photo>>
    suspend fun getTopics(): Result<List<Topic>>
    suspend fun getPhoto(photoId: String): Result<Photo>
    suspend fun reportDownload(downloadUrl: String)
}
