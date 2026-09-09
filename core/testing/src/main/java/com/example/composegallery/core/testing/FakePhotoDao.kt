package com.example.composegallery.core.testing

import androidx.paging.PagingSource
import com.example.composegallery.core.database.local.home.dao.PhotoDao
import com.example.composegallery.core.database.local.home.entity.PhotoEntity

class FakePhotoDao : PhotoDao {

    private val photos = mutableMapOf<String, PhotoEntity>()

    override fun getPagedPhotos(topicId: String): PagingSource<Int, PhotoEntity> {
        throw NotImplementedError("Fake PagingSource not implemented")
    }

    override suspend fun getPhotoById(photoId: String): PhotoEntity? {
        return photos[photoId]
    }

    override suspend fun insertPhotos(photos: List<PhotoEntity>): List<Long> {
        photos.forEach { this.photos[it.id] = it }
        return photos.map { 1L }
    }

    override suspend fun clearAll(topicId: String) {
        photos.clear()
    }

    override suspend fun getCount(topicId: String): Int {
        return photos.size
    }

    override suspend fun updatePagingOrder(photoId: String, topicId: String, pagingOrder: Int) {
        photos[photoId]?.let {
            photos[photoId] = it.copy(pagingOrder = pagingOrder)
        }
    }
}
