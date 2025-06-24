package com.example.composegallery.feature.gallery.data.model

import com.example.composegallery.feature.gallery.domain.model.Photo
import kotlinx.serialization.Serializable

@Serializable
data class UnsplashPhotoDto(
    val id: String,
    val urls: UrlsDto,
    val user: UserDto,
    val likes: Int = 0
)

internal fun UnsplashPhotoDto.toDomainModel(): Photo? {
    if (urls.thumb.isBlank() || urls.regular.isBlank() || user.name.isBlank()) return null

    return Photo(
        id = id,
        thumbUrl = urls.thumb,
        fullUrl = urls.full,
        authorName = user.name
    )
}

