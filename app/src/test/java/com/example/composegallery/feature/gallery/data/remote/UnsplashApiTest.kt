package com.example.composegallery.feature.gallery.data.remote

import com.google.common.truth.Truth.assertThat
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit

class UnsplashApiTest {

    private lateinit var server: MockWebServer
    private lateinit var api: UnsplashApi

    @Before
    fun setup() {
        server = MockWebServer()
        server.start()

        val contentType = "application/json".toMediaType()
        val json = Json { ignoreUnknownKeys = true }

        val retrofit = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()

        api = retrofit.create(UnsplashApi::class.java)
    }

    @After
    fun teardown() {
        server.shutdown()
    }

    @Test
    fun getPhotos_returnsParsedPhotosOnSuccess() = runTest {
        val fakeJson = """
            [
              {
                "id": "1",
                "width": 1000,
                "height": 800,
                "likes": 100,
                "urls": {
                  "thumb": "https://example.com/thumb.jpg",
                  "small": "https://example.com/small.jpg",
                  "regular": "https://example.com/regular.jpg",
                  "full": "https://example.com/full.jpg"
                },
                "user": {
                  "name": "Jane Doe",
                  "username": "jane_doe",
                  "location": "Kenya",
                  "profile_image": {
                    "small": "https://example.com/profile_small.jpg",
                    "medium": "https://example.com/profile_medium.jpg",
                    "large": "https://example.com/profile_large.jpg"
                  }
                }
              }
            ]
        """.trimIndent()

        server.enqueue(MockResponse().setBody(fakeJson).setResponseCode(200))

        val result = api.getPhotos(page = 1, perPage = 1)
        val request = server.takeRequest()

        assertThat(request.path).contains("/photos?page=1&per_page=1")
        assertThat(result).isNotEmpty()

        val photo = result.first()
        assertThat(photo.id).isEqualTo("1")
        assertThat(photo.user.name).isEqualTo("Jane Doe")
        assertThat(photo.urls.thumb).isEqualTo("https://example.com/thumb.jpg")
    }

    @Test
    fun getPhotos_returnsEmptyListOnEmptyResponse() = runTest {
        server.enqueue(MockResponse().setBody("[]").setResponseCode(200))

        val result = api.getPhotos(page = 1, perPage = 1)

        assertThat(result).isEmpty()
    }

    @Test
    fun getPhotos_handlesMalformedJsonGracefully() = runTest {
        val malformedJson =
            """ [{ "id": 1, "urls": { "thumb": 123 } }] """ // thumb should be string
        server.enqueue(MockResponse().setBody(malformedJson).setResponseCode(200))

        try {
            api.getPhotos(page = 1, perPage = 1)
            assert(false) // Should not reach here
        } catch (e: Exception) {
            assertThat(e).isInstanceOf(Exception::class.java)
        }
    }

    @Test
    fun getPhotos_failsOnHttp500() = runTest {
        server.enqueue(MockResponse().setResponseCode(500))

        try {
            api.getPhotos(page = 1, perPage = 1)
            assert(false) // Should throw
        } catch (e: Exception) {
            assertThat(e).isInstanceOf(retrofit2.HttpException::class.java)
            assertThat((e as retrofit2.HttpException).code()).isEqualTo(500)
        }
    }
}
