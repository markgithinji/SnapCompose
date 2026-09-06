package com.example.composegallery.feature.home.data.repository

import com.example.composegallery.core.model.Result
import com.example.composegallery.core.network.model.ExifDto
import com.example.composegallery.core.network.model.ProfileImageDto
import com.example.composegallery.core.network.model.UnsplashPhotoDto
import com.example.composegallery.core.network.model.UrlsDto
import com.example.composegallery.core.network.model.UserDto
import com.example.composegallery.core.model.Photo
import com.example.composegallery.core.database.local.AppDatabase
import com.example.composegallery.core.database.local.PhotoDao
import com.example.composegallery.core.database.local.PhotoEntity
import com.example.composegallery.core.database.local.toDomainModel
import com.example.composegallery.core.network.remote.UnsplashApi
import com.example.composegallery.core.util.StringProvider
import com.example.composegallery.core.network.repository.home.DefaultGalleryRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*
import kotlinx.coroutines.runBlocking

class DefaultGalleryRepositoryTest {

    private lateinit var api: UnsplashApi
    private lateinit var database: AppDatabase
    private lateinit var stringProvider: StringProvider
    private lateinit var repository: DefaultGalleryRepository

    @Before
    fun setup() {
        api = mock()
        database = mock()
        val photoDao = mock<PhotoDao>()
        whenever(database.photoDao()).thenReturn(photoDao)
        stringProvider = mock()
        repository = DefaultGalleryRepository(api, database, stringProvider)
    }

    @Test
    fun getPhoto_validResponse_returnsSuccess() = runTest {
        val dto = fakePhotoDto(id = "123")
        whenever(runBlocking { api.getPhoto("123") }).thenReturn(dto)

        val result = repository.getPhoto("123")

        assertThat(result is Result.Success<*>).isTrue()
        assertThat((result as Result.Success<Photo>).data.id).isEqualTo("123")
    }

    @Test
    fun getPhoto_optionalFieldsMissing_returnsSuccess() = runTest {
        val dto = fakePhotoDto("optional").copy(
            blurHash = null,
            description = null,
            altDescription = null,
            exif = null
        )
        whenever(runBlocking { api.getPhoto("optional") }).thenReturn(dto)

        val result = repository.getPhoto("optional")

        assertThat(result is Result.Success<*>).isTrue()
        val photo = (result as Result.Success<Photo>).data
        assertThat(photo.description).isNull()
        assertThat(photo.exif).isNull()
    }

    @Test
    fun getPhoto_zeroWidthAndHeight_returnsSuccess() = runTest {
        val dto = fakePhotoDto("zero-values").copy(width = 0, height = 0)
        whenever(runBlocking { api.getPhoto("zero-values") }).thenReturn(dto)

        val result = repository.getPhoto("zero-values")

        assertThat(result is Result.Success<*>).isTrue()
        val photo = (result as Result.Success<Photo>).data
        assertThat(photo.width).isEqualTo(0)
        assertThat(photo.height).isEqualTo(0)
    }

    @Test
    fun getPhoto_apiThrowsException_returnsError() = runTest {
        whenever(runBlocking { api.getPhoto("boom") }).thenThrow(RuntimeException("timeout"))
        whenever(stringProvider.get(any(), anyVararg())).thenReturn("Network error")

        val result = repository.getPhoto("boom")

        assertThat(result is Result.Error).isTrue()
        val message = (result as Result.Error).message
        assertThat(message.lowercase()).contains("network")
    }

    @Test
    fun getPhoto_apiReturnsNull_returnsError() = runTest {
        whenever(runBlocking { api.getPhoto("null") }).thenReturn(null)
        whenever(stringProvider.get(any(), anyVararg())).thenReturn("Unexpected null result")

        val result = repository.getPhoto("null")

        assertThat(result is Result.Error).isTrue()
        val message = (result as Result.Error).message
        assertThat(message.lowercase()).contains("unexpected")
    }

    @Test
    fun getPhoto_blankId_returnsError() = runTest {
        val dto = fakePhotoDto(id = "")
        whenever(runBlocking { api.getPhoto("blank") }).thenReturn(dto)
        whenever(stringProvider.get(any(), anyVararg())).thenReturn("Invalid photo data")

        val result = repository.getPhoto("blank")

        assertThat(result is Result.Error).isTrue()
        val message = (result as Result.Error).message
        assertThat(message.lowercase()).contains("invalid")
    }

    @Test
    fun getPhoto_returnsError_whenRequiredFieldsAreMissing() = runTest {
        val invalidUser = fakeUserDto().copy(
            name = "",
            profileImage = fakeUserDto().profileImage.copy(
                small = "",
                medium = "",
                large = ""
            )
        )
        val dto = fakePhotoDto(id = "invalid-fields").copy(user = invalidUser)
        whenever(runBlocking { api.getPhoto("invalid-fields") }).thenReturn(dto)
        whenever(stringProvider.get(any(), anyVararg())).thenReturn("Missing required fields")

        val result = repository.getPhoto("invalid-fields")

        assertThat(result is Result.Error).isTrue()
        val message = (result as Result.Error).message
        assertThat(message.lowercase()).contains("missing")
    }


    @Test
    fun getPhoto_cachedInDb_returnsCachedPhoto() = runTest {
        val photoId = "cached-123"
        val entity = mock<PhotoEntity>().apply {
            whenever(id).thenReturn(photoId)
            whenever(authorName).thenReturn("Cached Author")
            whenever(smallUrl).thenReturn("url")
            whenever(fullUrl).thenReturn("url")
            whenever(regularUrl).thenReturn("url")
            whenever(thumbUrl).thenReturn("url")
            whenever(authorProfileImageUrl).thenReturn("url")
            whenever(authorProfileImageMediumResUrl).thenReturn("url")
            whenever(authorProfileImageHighResUrl).thenReturn("url")
        }
        whenever(runBlocking { database.photoDao().getPhotoById(photoId) }).thenReturn(entity)

        val result = repository.getPhoto(photoId)

        assertThat(result is Result.Success<*>).isTrue()
        assertThat((result as Result.Success<Photo>).data.id).isEqualTo(photoId)
        assertThat(result.data.authorName).isEqualTo("Cached Author")
        verify(api, never()).getPhoto(any())
    }

    private fun fakePhotoDto(id: String = "123"): UnsplashPhotoDto {
        return UnsplashPhotoDto(
            id = id,
            width = 1000,
            height = 800,
            urls = fakeUrlsDto(),
            user = fakeUserDto(),
            likes = 42,
            blurHash = "LKO2?U%2Tw=w]~RBVZRi};RPxuwH",
            description = "A beautiful photo",
            altDescription = "Alt desc",
            createdAt = "2023-01-01T00:00:00Z",
            exif = fakeExifDto()
        )
    }

    private fun fakeUrlsDto() = UrlsDto(
        thumb = "https://example.com/thumb.jpg",
        small = "https://example.com/small.jpg",
        regular = "https://example.com/regular.jpg",
        full = "https://example.com/full.jpg"
    )

    private fun fakeUserDto() = UserDto(
        name = "Jane Doe",
        username = "jane_doe",
        location = "Kenya",
        profileImage = ProfileImageDto(
            small = "https://example.com/small.jpg",
            medium = "https://example.com/medium.jpg",
            large = "https://example.com/large.jpg"
        )
    )

    private fun fakeExifDto() = ExifDto(
        make = "Canon",
        model = "EOS R5",
        aperture = "f/2.8",
        exposureTime = "1/250",
        focalLength = "85mm",
        iso = 100
    )
}
