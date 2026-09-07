package com.example.composegallery.core.database.local

import androidx.room.TypeConverter
import com.example.composegallery.core.domain.model.Exif
import com.example.composegallery.core.domain.model.PhotoLocation
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class RoomConverters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromPhotoLocation(value: PhotoLocation?): String? {
        return value?.let { json.encodeToString(it) }
    }

    @TypeConverter
    fun toPhotoLocation(value: String?): PhotoLocation? {
        return value?.let { json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromExif(value: Exif?): String? {
        return value?.let { json.encodeToString(it) }
    }

    @TypeConverter
    fun toExif(value: String?): Exif? {
        return value?.let { json.decodeFromString(it) }
    }
}
