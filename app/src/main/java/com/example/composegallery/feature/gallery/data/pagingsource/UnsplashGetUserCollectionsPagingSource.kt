package com.example.composegallery.feature.gallery.data.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.composegallery.BuildConfig
import com.example.composegallery.feature.gallery.data.model.toDomainModel
import com.example.composegallery.feature.gallery.data.remote.UnsplashApi
import com.example.composegallery.feature.gallery.data.util.Result
import com.example.composegallery.feature.gallery.data.util.safeApiCall
import com.example.composegallery.feature.gallery.domain.model.Collection


class UnsplashGetUserCollectionsPagingSource(
    private val api: UnsplashApi,
    private val username: String
) : PagingSource<Int, Collection>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Collection> {
        val page = params.key ?: 1

        return when (val result = safeApiCall {
            api.getUserCollections(
                username = username,
                page = page,
                perPage = params.loadSize,
                clientId = BuildConfig.UNSPLASH_API_KEY
            ).map { it.toDomainModel() }
        }) {
            is Result.Success -> {
                LoadResult.Page(
                    data = result.data,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (result.data.isEmpty()) null else page + 1
                )
            }

            is Result.Error -> {
                LoadResult.Error(result.throwable ?: Exception(result.message))
            }
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Collection>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }
}