package com.example.composegallery.core.data.remote

import com.example.composegallery.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val newRequest = originalRequest.newBuilder()
            .addHeader("Authorization", "Client-ID ${BuildConfig.UNSPLASH_API_KEY.trim()}")
            .build()

        return chain.proceed(newRequest)
    }
}
