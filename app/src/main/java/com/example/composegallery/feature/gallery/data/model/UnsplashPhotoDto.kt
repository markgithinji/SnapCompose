package com.example.composegallery.feature.gallery.data.model

import com.example.composegallery.feature.gallery.domain.model.Photo
import kotlinx.serialization.Serializable

@Serializable
data class UnsplashPhotoDto(
    val id: String,
    val width: Int,
    val height: Int,
    val urls: UrlsDto,
    val user: UserDto,
    val likes: Int = 0
)

internal fun UnsplashPhotoDto.toDomainModel(): Photo? {
    if (
        urls.thumb.isBlank() ||
        urls.full.isBlank() ||
        user.name.isBlank() ||
        user.profileImage.small.isBlank()
    ) return null

    return Photo(
        id = id,
        width = width,
        height = height,
        thumbUrl = urls.thumb,
        fullUrl = urls.full,
        authorName = user.name,
        authorProfileImageUrl = user.profileImage.small
    )
}