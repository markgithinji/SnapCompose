package com.example.composegallery.core.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.composegallery.core.model.Result
import com.example.composegallery.core.data.remote.UnsplashApi
import com.example.composegallery.core.data.safeApiCall
import com.example.composegallery.core.util.StringProvider
import timber.log.Timber

abstract class BaseUnsplashPagingSource<T : Any>(
    private val stringProvider: StringProvider,
    private val api: suspend (page: Int, perPage: Int) -> List<T>
) : PagingSource<Int, T>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        val page = params.key ?: 1
        val pageSize = minOf(params.loadSize, UnsplashApi.DEFAULT_PAGE_SIZE)

        return when (val result = safeApiCall(stringProvider) {
            api(page, pageSize)
        }) {
            is Result.Success -> {
                Timber.tag("PagingSource").d("Loaded %d items for page %d", result.data.size, page)
                LoadResult.Page(
                    data = result.data,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (result.data.isEmpty()) null else page + 1
                )
            }

            is Result.Error -> {
                Timber.tag("PagingSource").e("Error loading page %d: %s", page, result.message)
                LoadResult.Error(
                    result.throwable ?: Exception(result.message)
                )
            }
        }
    }

    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        return state.anchorPosition?.let { pos ->
            state.closestPageToPosition(pos)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(pos)?.nextKey?.minus(1)
        }
    }
}
