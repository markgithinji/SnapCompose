package com.example.composegallery.core.testing

import com.example.composegallery.core.database.local.home.dao.PhotoRemoteKeyDao
import com.example.composegallery.core.database.local.home.entity.PhotoRemoteKeyEntity

class FakePhotoRemoteKeyDao : PhotoRemoteKeyDao {
    private val keys = mutableMapOf<String, PhotoRemoteKeyEntity>()

    override suspend fun insertAll(remoteKey: List<PhotoRemoteKeyEntity>) {
        remoteKey.forEach { keys["${it.photoId}_${it.topicId}"] = it }
    }

    override suspend fun getRemoteKeyByPhotoId(photoId: String, topicId: String): PhotoRemoteKeyEntity? {
        return keys["${photoId}_${topicId}"]
    }

    override suspend fun clearRemoteKeys(topicId: String) {
        val iterator = keys.entries.iterator()
        while (iterator.hasNext()) {
            if (iterator.next().value.topicId == topicId) {
                iterator.remove()
            }
        }
    }
}
