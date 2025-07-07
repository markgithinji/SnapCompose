package com.example.composegallery.feature.gallery.data.model

import com.example.composegallery.feature.gallery.domain.model.Photo
import com.example.composegallery.feature.gallery.domain.model.PhotoLocation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UnsplashPhotoDto(
    val id: String,
    val width: Int,
    val height: Int,
    val urls: UrlsDto,
    val user: UserDto,
    val likes: Int = 0,
    @SerialName("blur_hash") val blurHash: String? = null,
    val description: String? = null,
    @SerialName("alt_description") val altDescription: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    val exif: ExifDto? = null
)

internal fun UnsplashPhotoDto.toDomainModel(): Photo? {
    if (!isValid()) return null

    return Photo(
        id = id,
        width = width,
        height = height,
        thumbUrl = urls.thumb,
        fullUrl = urls.full,
        regularUrl = urls.regular,
        authorName = user.name,
        authorProfileImageUrl = user.profileImage.small,
        authorProfileImageMediumResUrl = user.profileImage.medium,
        authorProfileImageHighResUrl = user.profileImage.large,
        username = user.username,
        location = user.location.toPhotoLocation(),
        blurHash = blurHash,
        description = description ?: altDescription,
        createdAt = createdAt,
        exif = exif?.toDomainModel()
    )
}

private fun UnsplashPhotoDto.isValid(): Boolean {
    return urls.thumb.isNotBlank()
            && urls.full.isNotBlank()
            && urls.regular.isNotBlank()
            && user.name.isNotBlank()
            && user.profileImage.small.isNotBlank()
            && user.profileImage.medium.isNotBlank()
            && user.profileImage.large.isNotBlank()
}

private fun String?.toPhotoLocation(): PhotoLocation? {
    if (this.isNullOrBlank()) return null

    val parts = this.split(",").map { it.trim() }
    return when (parts.size) {
        2 -> PhotoLocation(city = parts[0], country = parts[1])
        1 -> PhotoLocation(city = null, country = parts[0])
        else -> null
    }
}

