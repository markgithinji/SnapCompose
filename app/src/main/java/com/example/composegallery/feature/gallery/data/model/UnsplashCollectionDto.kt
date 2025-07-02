package com.example.composegallery.feature.gallery.data.model

import com.example.composegallery.feature.gallery.domain.model.PhotoCollection
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UnsplashCollectionDto(
    val id: String,
    val title: String,
    val description: String? = null,
    @SerialName("total_photos") val totalPhotos: Int,
    @SerialName("cover_photo") val coverPhoto: UnsplashPhotoDto? = null,
    val user: UserDto
)

fun UnsplashCollectionDto.toDomainModel(): PhotoCollection {
    return PhotoCollection(
        id = id,
        title = title,
        description = description,
        totalPhotos = totalPhotos,
        coverPhoto = coverPhoto?.toDomainModel(),
        authorName = user.name
    )
}