package com.example.composegallery.core.data

import com.example.composegallery.R
import com.example.composegallery.core.util.StringProvider
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.CancellationException
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class ResultExtTest {

    private val stringProvider: StringProvider = mock()

    @Test
    fun safeApiCall_success_returnsSuccess() {
        val result = safeApiCall(stringProvider) { "data" }
        assertThat(result).isEqualTo(Result.Success("data"))
    }

    @Test
    fun safeApiCall_ioException_returnsError() {
        whenever(stringProvider.get(R.string.error_no_internet_connection)).thenReturn("No internet")
        
        val result = safeApiCall(stringProvider) { throw IOException() }
        
        assertThat(result).isInstanceOf(Result.Error::class.java)
        assertThat((result as Result.Error).message).isEqualTo("No internet")
    }

    @Test
    fun safeApiCall_httpException_returnsMappedError() {
        val response = Response.error<String>(404, mock())
        val exception = HttpException(response)
        whenever(stringProvider.get(R.string.error_not_found)).thenReturn("Not found")

        val result = safeApiCall(stringProvider) { throw exception }

        assertThat(result).isInstanceOf(Result.Error::class.java)
        assertThat((result as Result.Error).message).isEqualTo("Not found")
    }

    @Test(expected = CancellationException::class)
    fun safeApiCall_cancellationException_rethrows() {
        safeApiCall(stringProvider) { throw CancellationException() }
    }

    @Test
    fun safeDbCall_success_returnsSuccess() {
        val result = safeDbCall(stringProvider) { "data" }
        assertThat(result).isEqualTo(Result.Success("data"))
    }

    @Test
    fun safeDbCall_exception_returnsError() {
        whenever(stringProvider.get(R.string.error_database)).thenReturn("DB error")

        val result = safeDbCall(stringProvider) { throw RuntimeException() }

        assertThat(result).isInstanceOf(Result.Error::class.java)
        assertThat((result as Result.Error).message).isEqualTo("DB error")
    }
}
