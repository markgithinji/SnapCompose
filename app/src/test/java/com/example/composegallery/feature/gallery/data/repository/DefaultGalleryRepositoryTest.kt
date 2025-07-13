package com.example.composegallery.feature.gallery.data.repository

import com.example.composegallery.feature.gallery.data.model.ExifDto
import com.example.composegallery.feature.gallery.data.model.ProfileImageDto
import com.example.composegallery.feature.gallery.data.model.UnsplashPhotoDto
import com.example.composegallery.feature.gallery.data.model.UrlsDto
import com.example.composegallery.feature.gallery.data.model.UserDto
import com.example.composegallery.feature.gallery.data.remote.UnsplashApi
import com.example.composegallery.feature.gallery.data.util.Result
import com.example.composegallery.feature.gallery.util.StringProvider
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class DefaultGalleryRepositoryTest {

    private lateinit var api: UnsplashApi
    private lateinit var stringProvider: StringProvider
    private lateinit var repository: DefaultGalleryRepository

    @Before
    fun setup() {
        api = mock()
        stringProvider = mock()
        repository = DefaultGalleryRepository(api, stringProvider)
    }

    @Test
    fun getPhoto_validResponse_returnsSuccess() = runTest {
        val dto = fakePhotoDto(id = "123")
        whenever(api.getPhoto("123")).thenReturn(dto)

        val result = repository.getPhoto("123")

        assertThat(result).isInstanceOf(Result.Success::class.java)
        assertThat((result as Result.Success).data.id).isEqualTo("123")
    }

    @Test
    fun getPhoto_optionalFieldsMissing_returnsSuccess() = runTest {
        val dto = fakePhotoDto("optional").copy(
            blurHash = null,
            description = null,
            altDescription = null,
            exif = null
        )
        whenever(api.getPhoto("optional")).thenReturn(dto)

        val result = repository.getPhoto("optional")

        assertThat(result).isInstanceOf(Result.Success::class.java)
        val photo = (result as Result.Success).data
        assertThat(photo.description).isNull()
        assertThat(photo.exif).isNull()
    }

    @Test
    fun getPhoto_zeroWidthAndHeight_returnsSuccess() = runTest {
        val dto = fakePhotoDto("zero-values").copy(width = 0, height = 0)
        whenever(api.getPhoto("zero-values")).thenReturn(dto)

        val result = repository.getPhoto("zero-values")

        assertThat(result).isInstanceOf(Result.Success::class.java)
        val photo = (result as Result.Success).data
        assertThat(photo.width).isEqualTo(0)
        assertThat(photo.height).isEqualTo(0)
    }

    @Test
    fun getPhoto_apiThrowsException_returnsError() = runTest {
        whenever(api.getPhoto("boom")).thenThrow(RuntimeException("timeout"))
        whenever(stringProvider.get(any())).thenReturn("Network error")

        val result = repository.getPhoto("boom")

        assertThat(result).isInstanceOf(Result.Error::class.java)
        val message = (result as Result.Error).message
        assertThat(message.lowercase()).contains("network")
    }

    @Test
    fun getPhoto_apiReturnsNull_returnsError() = runTest {
        whenever(api.getPhoto("null")).thenReturn(null)
        whenever(stringProvider.get(any())).thenReturn("Unexpected null result")

        val result = repository.getPhoto("null")

        assertThat(result).isInstanceOf(Result.Error::class.java)
        val message = (result as Result.Error).message
        assertThat(message.lowercase()).contains("unexpected")
    }

    @Test
    fun getPhoto_blankId_returnsError() = runTest {
        val dto = fakePhotoDto(id = "")
        whenever(api.getPhoto("blank")).thenReturn(dto)
        whenever(stringProvider.get(any())).thenReturn("Invalid photo data")

        val result = repository.getPhoto("blank")

        assertThat(result).isInstanceOf(Result.Error::class.java)
        val message = (result as Result.Error).message
        assertThat(message.lowercase()).contains("invalid")
    }

    @Test
    fun getPhoto_returnsError_whenRequiredFieldsAreMissing() = runTest {
        val invalidUser = fakeUserDto().copy(
            name = "", // invalid field
            profileImage = fakeUserDto().profileImage.copy(
                small = "", // invalid field
                medium = "",
                large = ""
            )
        )
        val dto = fakePhotoDto(id = "invalid-fields").copy(user = invalidUser)
        whenever(api.getPhoto("invalid-fields")).thenReturn(dto)
        whenever(stringProvider.get(any())).thenReturn("Missing required fields")

        val result = repository.getPhoto("invalid-fields")

        assertThat(result).isInstanceOf(Result.Error::class.java)
        val message = (result as Result.Error).message
        assertThat(message.lowercase()).contains("missing")
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
