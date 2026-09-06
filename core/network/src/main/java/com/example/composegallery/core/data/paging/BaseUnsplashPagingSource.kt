package com.example.composegallery.core.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.composegallery.core.data.AppException
import com.example.composegallery.core.util.StringProvider
import com.example.composegallery.core.ui.R

abstract class BaseUnsplashPagingSource<T : Any>(
    private val stringProvider: StringProvider,
    private val api: suspend (page: Int, perPage: Int) -> List<T>
) : PagingSource<Int, T>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        val page = params.key ?: 1
        return try {
            val response = api(page, params.loadSize)
            LoadResult.Page(
                data = response,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (response.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(
                AppException(
                    message = stringProvider.get(R.string.error_unexpected),
                    cause = e
                )
            )
        }
    }

    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
