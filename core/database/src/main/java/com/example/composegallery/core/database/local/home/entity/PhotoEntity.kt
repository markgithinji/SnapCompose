package com.example.composegallery.core.database.local.home.entity

import androidx.room.Embedded
import androidx.room.Entity
import com.example.composegallery.core.domain.model.Exif
import com.example.composegallery.core.domain.model.Photo
import com.example.composegallery.core.domain.model.PhotoLocation

@Entity(
    tableName = "photos",
    primaryKeys = ["id", "topicId"]
)
data class PhotoEntity(
    val id: String,
    val topicId: String,
    val width: Int,
    val height: Int,
    val thumbUrl: String,
    val smallUrl: String,
    val regularUrl: String,
    val fullUrl: String,
    val authorName: String,
    val authorProfileImageUrl: String,
    val authorProfileImageMediumResUrl: String,
    val authorProfileImageHighResUrl: String,
    val authorUnsplashUrl: String?,
    val username: String?,
    val downloadLocationUrl: String?,
    val pagingOrder: Int = 0,
    val location: PhotoLocation?,
    val blurHash: String? = null,
    val description: String? = null,
    val createdAt: String? = null,
    val exif: Exif? = null
)

fun PhotoEntity.toDomainModel(): Photo {
    return Photo(
        id = id,
        width = width,
        height = height,
        thumbUrl = thumbUrl,
        smallUrl = smallUrl,
        regularUrl = regularUrl,
        fullUrl = fullUrl,
        authorName = authorName,
        authorProfileImageUrl = authorProfileImageUrl,
        authorProfileImageMediumResUrl = authorProfileImageMediumResUrl,
        authorProfileImageHighResUrl = authorProfileImageHighResUrl,
        authorUnsplashUrl = authorUnsplashUrl,
        username = username,
        downloadLocationUrl = downloadLocationUrl,
        location = location,
        blurHash = blurHash,
        description = description,
        createdAt = createdAt,
        exif = exif,
        debugOrder = pagingOrder
    )
}

fun Photo.toEntity(topicId: String, pagingOrder: Int = 0): PhotoEntity {
    return PhotoEntity(
        id = id,
        topicId = topicId,
        width = width,
        height = height,
        thumbUrl = thumbUrl,
        smallUrl = smallUrl,
        regularUrl = regularUrl,
        fullUrl = fullUrl,
        authorName = authorName,
        authorProfileImageUrl = authorProfileImageUrl,
        authorProfileImageMediumResUrl = authorProfileImageMediumResUrl,
        authorProfileImageHighResUrl = authorProfileImageHighResUrl,
        authorUnsplashUrl = authorUnsplashUrl,
        username = username,
        downloadLocationUrl = downloadLocationUrl,
        location = location,
        blurHash = blurHash,
        description = description,
        createdAt = createdAt,
        exif = exif,
        pagingOrder = if (debugOrder != -1) debugOrder else pagingOrder
    )
}
