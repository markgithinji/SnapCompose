package com.example.composegallery.feature.gallery.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.composegallery.feature.gallery.domain.model.RecentSearch
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentSearchDao {
    @Query("SELECT * FROM recent_searches ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentSearches(limit: Int = 10): Flow<List<RecentSearch>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearch(search: RecentSearch)

    @Query("DELETE FROM recent_searches WHERE query = :query")
    suspend fun deleteSearch(query: String)

    @Query("DELETE FROM recent_searches")
    suspend fun clearSearches()
}