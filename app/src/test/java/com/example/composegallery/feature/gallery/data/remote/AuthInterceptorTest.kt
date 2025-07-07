package com.example.composegallery.feature.gallery.data.remote

import com.example.composegallery.BuildConfig
import com.google.common.truth.Truth.assertThat
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
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
        mockWebServer.shutdown()
    }

    @Test
    fun intercept_addsClientIdQueryParameter() {
        val interceptor = AuthInterceptor()

        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        mockWebServer.enqueue(MockResponse().setBody("OK"))

        val request = Request.Builder()
            .url(mockWebServer.url("/photos"))
            .build()

        client.newCall(request).execute()

        val recordedRequest = mockWebServer.takeRequest()
        val requestUrl = recordedRequest.requestUrl

        assertThat(requestUrl?.encodedPath).isEqualTo("/photos")
        val clientId = requestUrl?.queryParameter("client_id")
        assertThat(clientId).isNotNull()
        assertThat(clientId).isEqualTo(BuildConfig.UNSPLASH_API_KEY)
    }

    @Test
    fun intercept_doesNotAddClientId_withoutInterceptor() {
        val client = OkHttpClient.Builder().build()

        mockWebServer.enqueue(MockResponse().setBody("OK"))

        val request = Request.Builder()
            .url(mockWebServer.url("/photos"))
            .build()

        client.newCall(request).execute()

        val recordedRequest = mockWebServer.takeRequest()
        val clientId = recordedRequest.requestUrl?.queryParameter("client_id")

        assertThat(clientId).isNull()
    }
}
