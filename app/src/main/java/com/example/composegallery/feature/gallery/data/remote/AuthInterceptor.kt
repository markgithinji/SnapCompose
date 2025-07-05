package com.example.composegallery.feature.gallery.data.remote

import com.example.composegallery.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

/**
 * An [Interceptor] that adds the Unsplash API key as a query parameter to every outgoing request.
 *
 * This interceptor is designed to be added to an OkHttpClient instance. It automatically
 * appends the `client_id` query parameter with the value of `BuildConfig.UNSPLASH_API_KEY`
 * to the URL of each request before it is sent. This is a common way to handle API key
 * authentication for services like Unsplash.
 *
 * **Usage:**
 * ```kotlin
 * val okHttpClient = OkHttpClient.Builder()
 *     .addInterceptor(AuthInterceptor())
 *     .build()
 *
 * val retrofit = Retrofit.Builder()
 *     .baseUrl("https://api.unsplash.com/")
 *     .client(okHttpClient)
 *     .addConverterFactory(GsonConverterFactory.create())
 *     .build()
 * ```
 */
class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalUrl = originalRequest.url

        val newUrl = originalUrl.newBuilder()
            .addQueryParameter("client_id", BuildConfig.UNSPLASH_API_KEY)
            .build()

        val newRequest = originalRequest.newBuilder()
            .url(newUrl)
            .build()

        return chain.proceed(newRequest)
    }
}