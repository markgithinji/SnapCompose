package com.example.composegallery.core.network.remote

import com.example.composegallery.core.network.BuildConfig
import com.google.common.truth.Truth.assertThat
import okhttp3.OkHttpClient
import okhttp3.Request
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test

class AuthInterceptorTest {

    private lateinit var mockWebServer: MockWebServer

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
    }

    @After
    fun tearDown() {
        mockWebServer.close()
    }

    @Test
    fun intercept_addsAuthorizationHeader() {
        val interceptor = AuthInterceptor()

        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        mockWebServer.enqueue(MockResponse.Builder().body("OK").build())

        val request = Request.Builder()
            .url(mockWebServer.url("/photos"))
            .build()

        client.newCall(request).execute()

        val recordedRequest = mockWebServer.takeRequest()
        val authHeader = recordedRequest.headers["Authorization"]

        assertThat(authHeader).isEqualTo("Client-ID ${BuildConfig.UNSPLASH_API_KEY.trim()}")
    }

    @Test
    fun intercept_doesNotAddAuthorization_withoutInterceptor() {
        val client = OkHttpClient.Builder().build()

        mockWebServer.enqueue(MockResponse.Builder().body("OK").build())

        val request = Request.Builder()
            .url(mockWebServer.url("/photos"))
            .build()

        client.newCall(request).execute()

        val recordedRequest = mockWebServer.takeRequest()
        val authHeader = recordedRequest.headers["Authorization"]

        assertThat(authHeader).isNull()
    }
}
