package com.example.composegallery.feature.gallery.data.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.composegallery.feature.gallery.domain.model.Exif
import com.example.composegallery.feature.gallery.domain.model.Photo
import com.example.composegallery.feature.gallery.domain.model.PhotoLocation

@Entity(tableName = "photos")
data class PhotoEntity(
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
    val pagingOrder: Int = 0,
    @Embedded(prefix = "location_") val location: PhotoLocation?,
    val blurHash: String? = null,
    val description: String? = null,
    val createdAt: String? = null,
    @Embedded(prefix = "exif_") val exif: Exif? = null
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

fun Photo.toEntity(pagingOrder: Int = 0): PhotoEntity {
    return PhotoEntity(
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
        pagingOrder = if (debugOrder != -1) debugOrder else pagingOrder
    )
}
