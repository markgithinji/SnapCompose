package com.example.composegallery.core.network

import com.example.composegallery.core.common.Result
import com.example.composegallery.core.common.safeApiCall
import com.example.composegallery.core.testing.FakeStringProvider
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ResultExtTest {

    private val stringProvider = FakeStringProvider()

    @Test
    fun safeApiCall_success_returnsSuccess() {
        val data = "Success"
        val result = safeApiCall(stringProvider) { data }

        assertThat(result).isInstanceOf(Result.Success::class.java)
        assertThat((result as Result.Success).data).isEqualTo(data)
    }

    @Test
    fun safeApiCall_exception_returnsError() {
        val result = safeApiCall(stringProvider) {
            throw Exception("Boom")
        }

        assertThat(result).isInstanceOf(Result.Error::class.java)
        assertThat((result as Result.Error).message).isEqualTo("Fake string")
    }
}
