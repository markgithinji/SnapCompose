package com.example.composegallery.feature.gallery.data.model

import com.example.composegallery.feature.gallery.domain.model.Exif
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents the EXIF (Exchangeable image file format) data associated with an image.
 *
 * This data class is designed to be serializable and holds common EXIF information
 * such as camera make, model, aperture, exposure time, focal length, and ISO.
 *
 * @property make The manufacturer of the camera or input device (e.g., "Canon", "Nikon").
 * @property model The model name or number of the camera or input device (e.g., "EOS R5", "Z7 II").
 * @property aperture The aperture value of the lens when the image was taken (e.g., "f/2.8", "F8").
 * @property exposureTime The exposure time of the image, often expressed as a fraction of a second (e.g., "1/1000", "30").
 *                        Serialized as "exposure_time".
 * @property focalLength The focal length of the lens in millimeters (e.g., "50mm", "200").
 *                         Serialized as "focal_length".
 * @property iso The ISO speed rating used when the image was taken (e.g., 100, 800, 6400).
 */
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
