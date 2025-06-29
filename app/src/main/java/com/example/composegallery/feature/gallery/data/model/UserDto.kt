package com.example.composegallery.feature.gallery.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val name: String,
    @SerialName("profile_image")
    val profileImage: ProfileImageDto,
    @SerialName("instagram_username") val instagramUsername: String? = null,
    val location: String? = null
)