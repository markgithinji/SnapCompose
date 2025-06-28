package com.example.composegallery.feature.gallery.data.model

import com.example.composegallery.feature.gallery.domain.model.Exif
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExifDto(
    val make: String? = null,
    val model: String? = null,
    val aperture: String? = null,
    @SerialName("exposure_time") val exposureTime: String? = null,
    @SerialName("focal_length") val focalLength: String? = null,
    val iso: Int? = null
)

internal fun ExifDto.toDomainModel(): Exif {
    return Exif(
        make = make,
        model = model,
        aperture = aperture,
        shutterSpeed = exposureTime,
        focalLength = focalLength,
        iso = iso
    )
}
