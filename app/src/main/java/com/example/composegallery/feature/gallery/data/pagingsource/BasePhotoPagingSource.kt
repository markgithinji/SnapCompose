package com.example.composegallery.feature.gallery.data.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.composegallery.feature.gallery.data.util.Result
import com.example.composegallery.feature.gallery.data.util.safeApiCall

abstract class BasePhotoPagingSource<T : Any>(
    private val apiCall: suspend (page: Int, perPage: Int) -> List<T>
) : PagingSource<Int, T>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        val page = params.key ?: 1
        val pageSize = minOf(params.loadSize, MAX_PAGE_SIZE)

        return when (val result = safeApiCall {
            apiCall(page, pageSize)
        }) {
            is Result.Success -> LoadResult.Page(
                data = result.data,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (result.data.isEmpty()) null else page + 1
            )

            is Result.Error -> LoadResult.Error(result.throwable ?: Exception(result.message))
        }
    }

    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        return state.anchorPosition?.let { pos ->
            state.closestPageToPosition(pos)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(pos)?.nextKey?.minus(1)
        }
    }

    companion object {
        private const val MAX_PAGE_SIZE = 20
    }
}