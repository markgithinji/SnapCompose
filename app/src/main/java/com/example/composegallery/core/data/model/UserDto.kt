package com.example.composegallery.core.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val name: String,
    @SerialName("profile_image")
    val profileImage: ProfileImageDto,
    val links: UserLinksDto? = null,
    val username: String? = null,
    val location: String? = null
)

@Serializable
data class UserLinksDto(
    val self: String,
    val html: String,
    val photos: String? = null,
    val likes: String? = null,
    val portfolio: String? = null
)
