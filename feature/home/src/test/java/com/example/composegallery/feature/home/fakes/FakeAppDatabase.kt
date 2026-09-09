package com.example.composegallery.feature.home.fakes

import com.example.composegallery.core.database.local.AppDatabase
import com.example.composegallery.core.database.local.home.dao.FavoritePhotoDao
import com.example.composegallery.core.database.local.home.dao.PhotoDao
import com.example.composegallery.core.database.local.home.dao.PhotoRemoteKeyDao
import com.example.composegallery.core.database.local.home.dao.TopicCacheMetadataDao
import org.mockito.kotlin.mock

abstract class FakeAppDatabase : AppDatabase() {
    private val photoDao = FakePhotoDao()
    
    override fun photoDao(): PhotoDao = photoDao
    
    // Mocks for DAOs we don't need to fake yet
    override fun favoritePhotoDao(): FavoritePhotoDao = mock()
    override fun photoRemoteKeyDao(): PhotoRemoteKeyDao = mock()
    override fun topicCacheMetadataDao(): TopicCacheMetadataDao = mock()
    
    override fun clearAllTables() {
        // No-op
    }
}
