package com.example.composegallery.feature.gallery.data.model

import com.example.composegallery.feature.gallery.domain.model.Topic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UnsplashTopicDto(
    val id: String,
    val slug: String,
    val title: String,
    val description: String? = null,
    @SerialName("total_photos") val totalPhotos: Int
)

fun UnsplashTopicDto.toDomainModel(): Topic {
    return Topic(
        id = id,
        slug = slug,
        title = title,
        description = description,
        totalPhotos = totalPhotos
    )
}
