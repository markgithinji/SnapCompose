package com.example.composegallery.core.testing

import com.example.composegallery.core.database.local.AppDatabase
import com.example.composegallery.core.database.local.home.dao.FavoritePhotoDao
import com.example.composegallery.core.database.local.home.dao.PhotoDao
import com.example.composegallery.core.database.local.home.dao.PhotoRemoteKeyDao
import com.example.composegallery.core.database.local.home.dao.TopicCacheMetadataDao
import com.example.composegallery.core.database.local.search.dao.RecentSearchDao
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

/**
 * A helper to create a mock [AppDatabase] that returns fakes for all its DAOs.
 */
fun createFakeAppDatabase(
    recentSearchDao: RecentSearchDao = FakeRecentSearchDao(),
    photoDao: PhotoDao = FakePhotoDao(),
    photoRemoteKeyDao: PhotoRemoteKeyDao = FakePhotoRemoteKeyDao(),
    favoritePhotoDao: FavoritePhotoDao = FakeFavoritePhotoDao(),
    topicCacheMetadataDao: TopicCacheMetadataDao = FakeTopicCacheMetadataDao()
): AppDatabase {
    val db = mock<AppDatabase>()
    whenever(db.recentSearchDao()).thenReturn(recentSearchDao)
    whenever(db.photoDao()).thenReturn(photoDao)
    whenever(db.photoRemoteKeyDao()).thenReturn(photoRemoteKeyDao)
    whenever(db.favoritePhotoDao()).thenReturn(favoritePhotoDao)
    whenever(db.topicCacheMetadataDao()).thenReturn(topicCacheMetadataDao)
    return db
}
