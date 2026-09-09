package com.example.composegallery.feature.home.data.repository

import com.example.composegallery.core.common.Result
import com.example.composegallery.core.network.model.ExifDto
import com.example.composegallery.core.network.model.ProfileImageDto
import com.example.composegallery.core.network.model.UnsplashPhotoDto
import com.example.composegallery.core.network.model.UrlsDto
import com.example.composegallery.core.network.model.UserDto
import com.example.composegallery.core.domain.model.Photo
import com.example.composegallery.core.database.local.home.entity.PhotoEntity
import com.example.composegallery.feature.home.data.DefaultGalleryRepository
import com.example.composegallery.core.testing.FakePhotoDao
import com.example.composegallery.core.testing.FakeStringProvider
import com.example.composegallery.core.testing.FakeUnsplashApi
import com.example.composegallery.core.testing.createFakeAppDatabase
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DefaultGalleryRepositoryTest {

    private val api = FakeUnsplashApi()
    private val photoDao = FakePhotoDao()
    private val database = createFakeAppDatabase(photoDao = photoDao)
    private val stringProvider = FakeStringProvider()
    private lateinit var repository: DefaultGalleryRepository

    @Before
    fun setup() {
        repository = DefaultGalleryRepository(api, database, stringProvider)
    }

    @Test
    fun getPhoto_validResponse_returnsSuccess() = runTest {
        val dto = fakePhotoDto(id = "123")
        api.setPhotoResult(dto)

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
        api.setPhotoResult(dto)

        val result = repository.getPhoto("optional")

        assertThat(result is Result.Success<*>).isTrue()
        val photo = (result as Result.Success<Photo>).data
        assertThat(photo.description).isNull()
        assertThat(photo.exif).isNull()
    }

    @Test
    fun getPhoto_zeroWidthAndHeight_returnsSuccess() = runTest {
        val dto = fakePhotoDto("zero-values").copy(width = 0, height = 0)
        api.setPhotoResult(dto)

        val result = repository.getPhoto("zero-values")

        assertThat(result is Result.Success<*>).isTrue()
        val photo = (result as Result.Success<Photo>).data
        assertThat(photo.width).isEqualTo(0)
        assertThat(photo.height).isEqualTo(0)
    }

    @Test
    fun getPhoto_apiThrowsException_returnsError() = runTest {
        api.setException(RuntimeException("timeout"))

        val result = repository.getPhoto("boom")

        assertThat(result is Result.Error).isTrue()
        val message = (result as Result.Error).message
        assertThat(message.lowercase()).contains("fake") // FakeStringProvider returns "Fake string"
    }

    @Test
    fun getPhoto_apiReturnsNull_returnsError() = runTest {
        api.setPhotoResult(null)

        val result = repository.getPhoto("null")

        assertThat(result is Result.Error).isTrue()
    }

    @Test
    fun getPhoto_blankId_returnsError() = runTest {
        val dto = fakePhotoDto(id = "")
        api.setPhotoResult(dto)

        val result = repository.getPhoto("blank")

        assertThat(result is Result.Error).isTrue()
    }

    @Test
    fun getPhoto_cachedInDb_returnsCachedPhoto() = runTest {
        val photoId = "cached-123"
        val entity = PhotoEntity(
            id = photoId,
            topicId = "editorial",
            width = 100,
            height = 100,
            thumbUrl = "url",
            smallUrl = "url",
            regularUrl = "url",
            fullUrl = "url",
            authorName = "Cached Author",
            authorProfileImageUrl = "url",
            authorProfileImageMediumResUrl = "url",
            authorProfileImageHighResUrl = "url",
            authorUnsplashUrl = null,
            username = null,
            downloadLocationUrl = null,
            pagingOrder = 0,
            location = null,
            blurHash = null,
            description = null,
            createdAt = null,
            exif = null
        )
        photoDao.insertPhotos(listOf(entity))

        val result = repository.getPhoto(photoId)

        assertThat(result is Result.Success<*>).isTrue()
        assertThat((result as Result.Success<Photo>).data.id).isEqualTo(photoId)
        assertThat(result.data.authorName).isEqualTo("Cached Author")
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
