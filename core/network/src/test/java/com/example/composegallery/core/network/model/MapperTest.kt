package com.example.composegallery.core.network.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MapperTest {

    @Test
    fun unsplashPhotoDto_toDomainModel_mapsCorrecty() {
        val dto = UnsplashPhotoDto(
            id = "1",
            width = 100,
            height = 100,
            urls = UrlsDto(thumb = "t", small = "s", regular = "r", full = "f"),
            user = UserDto(
                name = "Author",
                profileImage = ProfileImageDto(small = "s", medium = "m", large = "l"),
                username = "author_username",
                location = "London"
            ),
            description = "desc",
            blurHash = "hash"
        )

        val domain = dto.toDomainModel()

        assertThat(domain).isNotNull()
        assertThat(domain?.id).isEqualTo("1")
        assertThat(domain?.authorName).isEqualTo("Author")
        assertThat(domain?.location?.country).isEqualTo("London")
        assertThat(domain?.description).isEqualTo("desc")
    }

    @Test
    fun unsplashPhotoDto_toDomainModel_returnsNull_whenInvalid() {
        val dto = UnsplashPhotoDto(
            id = "",
            width = 100,
            height = 100,
            urls = UrlsDto(thumb = "", small = "", regular = "", full = ""),
            user = UserDto(
                name = "",
                profileImage = ProfileImageDto(small = "", medium = "", large = ""),
            )
        )

        val domain = dto.toDomainModel()

        assertThat(domain).isNull()
    }
}
