package com.example.composegallery.feature.gallery.data.model

import com.example.composegallery.feature.gallery.domain.model.UnsplashUser
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UnsplashUserDto(
    val id: String,
    val username: String,
    val name: String,
    val bio: String? = null,
    val location: String? = null,
    @SerialName("profile_image") val profileImage: ProfileImageDto,
    @SerialName("instagram_username") val instagramUsername: String? = null,
    @SerialName("total_photos") val totalPhotos: Int = 0,
    @SerialName("total_likes") val totalLikes: Int = 0,
    @SerialName("total_collections") val totalCollections: Int = 0
)

fun UnsplashUserDto.toDomainModel(): UnsplashUser = UnsplashUser(
    id = id,
    username = username,
    name = name,
    bio = bio,
    location = location,
    profileImageSmall = profileImage.small,
    profileImageMedium = profileImage.medium,
    profileImageLarge = profileImage.large,
    instagramUsername = instagramUsername,
    totalPhotos = totalPhotos,
    totalLikes = totalLikes,
    totalCollections = totalCollections
)
