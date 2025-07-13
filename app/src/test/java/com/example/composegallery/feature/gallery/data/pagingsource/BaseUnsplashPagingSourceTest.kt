package com.example.composegallery.feature.gallery.data.pagingsource

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.composegallery.feature.gallery.util.StringProvider
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class BaseUnsplashPagingSourceTest {

    private lateinit var pagingSource: BaseUnsplashPagingSource<String>
    private val stringProvider = mock<StringProvider>()

    private val fakeDataPage1 = listOf("item1", "item2", "item3")
    private val fakeDataPage2 = listOf("item4", "item5")

    private var apiMock: suspend (Int, Int) -> List<String> = { _, _ -> emptyList() }

    @Before
    fun setup() {
        pagingSource = object : BaseUnsplashPagingSource<String>(stringProvider, apiMock) {}
    }

    @Test
    fun load_returnsPageOnSuccess() = runTest {
        apiMock = { page, _ ->
            when (page) {
                1 -> fakeDataPage1
                2 -> fakeDataPage2
                else -> emptyList()
            }
        }
        pagingSource = object : BaseUnsplashPagingSource<String>(stringProvider, apiMock) {}

        val params = PagingSource.LoadParams.Refresh<Int>(
            key = null,
            loadSize = 20,
            placeholdersEnabled = false
        )

        val result = pagingSource.load(params)

        assertThat(result).isInstanceOf(PagingSource.LoadResult.Page::class.java)
        val page = result as PagingSource.LoadResult.Page
        assertThat(page.data).containsExactlyElementsIn(fakeDataPage1).inOrder()
        assertThat(page.prevKey).isNull()
        assertThat(page.nextKey).isEqualTo(2)
    }

    @Test
    fun load_returnsErrorOnException() = runTest {
        whenever(stringProvider.get(any())).thenReturn("Something went wrong")

        apiMock = { _, _ -> throw RuntimeException("Unexpected error") }
        pagingSource = object : BaseUnsplashPagingSource<String>(stringProvider, apiMock) {}

        val params = PagingSource.LoadParams.Refresh(
            key = 1,
            loadSize = 20,
            placeholdersEnabled = false
        )

        val result = pagingSource.load(params)

        assertThat(result).isInstanceOf(PagingSource.LoadResult.Error::class.java)
        val error = result as PagingSource.LoadResult.Error
        assertThat(error.throwable).hasMessageThat().contains("Unexpected error")
    }

    @Test
    fun getRefreshKey_returnsNull_whenAnchorPositionIsNull() {
        val state = PagingState<Int, String>(
            pages = emptyList(),
            anchorPosition = null,
            config = PagingConfig(pageSize = 20),
            leadingPlaceholderCount = 0
        )

        val refreshKey = pagingSource.getRefreshKey(state)

        assertThat(refreshKey).isNull()
    }

    @Test
    fun load_returnsPageWithNullNextKeyWhenDataIsEmpty() = runTest {
        apiMock = { _, _ -> emptyList() }
        pagingSource = object : BaseUnsplashPagingSource<String>(stringProvider, apiMock) {}

        val params = PagingSource.LoadParams.Refresh<Int>(
            key = 3,
            loadSize = 20,
            placeholdersEnabled = false
        )

        val result = pagingSource.load(params)

        assertThat(result).isInstanceOf(PagingSource.LoadResult.Page::class.java)
        val page = result as PagingSource.LoadResult.Page
        assertThat(page.data).isEmpty()
        assertThat(page.prevKey).isEqualTo(2)
        assertThat(page.nextKey).isNull()
    }

    @Test
    fun getRefreshKey_returnsCorrectKey() {
        val anchorPosition = 5
        val state = PagingState<Int, String>(
            pages = listOf(
                PagingSource.LoadResult.Page(data = fakeDataPage1, prevKey = null, nextKey = 2),
                PagingSource.LoadResult.Page(data = fakeDataPage2, prevKey = 1, nextKey = null)
            ),
            anchorPosition = anchorPosition,
            config = PagingConfig(pageSize = 20),
            leadingPlaceholderCount = 0
        )

        val refreshKey = pagingSource.getRefreshKey(state)

        assertThat(refreshKey).isEqualTo(2)
    }
}
