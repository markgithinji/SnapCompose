package com.example.composegallery.core.database.local.home.entity

import androidx.room.Entity

@Entity(
    tableName = "photo_remote_keys",
    primaryKeys = ["photoId", "topicId"]
)
data class PhotoRemoteKeyEntity(
    val photoId: String,
    val topicId: String,
    val prevPage: Int?,
    val nextPage: Int?
)
