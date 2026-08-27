package com.example.composegallery.feature.gallery.domain.repository

import androidx.paging.PagingData
import com.example.composegallery.feature.gallery.data.util.Result
import com.example.composegallery.feature.gallery.domain.model.Photo
import com.example.composegallery.feature.gallery.domain.model.Topic
import kotlinx.coroutines.flow.Flow

interface GalleryRepository {
    fun getPagedPhotos(): Flow<PagingData<Photo>>
    fun getTopicPagedPhotos(topicIdOrSlug: String): Flow<PagingData<Photo>>
    suspend fun getTopics(): Result<List<Topic>>
    suspend fun getPhoto(photoId: String): Result<Photo>
    suspend fun reportDownload(downloadUrl: String)
}

