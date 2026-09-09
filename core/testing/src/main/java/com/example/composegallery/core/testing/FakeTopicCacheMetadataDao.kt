package com.example.composegallery.core.testing

import com.example.composegallery.core.database.local.home.dao.TopicCacheMetadataDao
import com.example.composegallery.core.database.local.home.entity.TopicCacheMetadataEntity

class FakeTopicCacheMetadataDao : TopicCacheMetadataDao {
    private val metadata = mutableMapOf<String, TopicCacheMetadataEntity>()

    override suspend fun insertMetadata(metadata: TopicCacheMetadataEntity) {
        this.metadata[metadata.topicId] = metadata
    }

    override suspend fun getMetadata(topicId: String): TopicCacheMetadataEntity? {
        return metadata[topicId]
    }

    override suspend fun deleteMetadata(topicId: String) {
        metadata.remove(topicId)
    }
}
