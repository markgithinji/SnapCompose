package com.example.composegallery.core.database.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.composegallery.core.model.Exif
import com.example.composegallery.core.model.Photo
import com.example.composegallery.core.model.PhotoLocation

@Entity(tableName = "favorites")
data class FavoritePhotoEntity(
    @PrimaryKey val id: String,
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
    val location: PhotoLocation?,
    val blurHash: String? = null,
    val description: String? = null,
    val createdAt: String? = null,
    val exif: Exif? = null,
    val favoritedAt: Long
)

fun FavoritePhotoEntity.toDomainModel(): Photo {
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
        exif = exif
    )
}

fun Photo.toFavoriteEntity(): FavoritePhotoEntity {
    return FavoritePhotoEntity(
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
        favoritedAt = System.currentTimeMillis()
    )
}
