package com.example.composegallery.feature.gallery.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photo_remote_keys")
data class PhotoRemoteKeyEntity(
    @PrimaryKey val photoId: String,
    val prevPage: Int?,
    val nextPage: Int?
)
