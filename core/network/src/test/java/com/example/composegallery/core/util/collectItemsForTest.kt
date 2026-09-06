package com.example.composegallery.core.util

import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield

private class NoopListCallback : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}

/**
 * Collect items from PagingData in a test-safe way using a provided dispatcher.
 */
suspend fun <T : Any> Flow<PagingData<T>>.collectItemsForTest(
    diffCallback: DiffUtil.ItemCallback<T>,
    dispatcher: CoroutineDispatcher
): List<T> = coroutineScope {
    val pagingData = first()

    val differ = AsyncPagingDataDiffer(
        diffCallback = diffCallback,
        updateCallback = NoopListCallback(),
        workerDispatcher = dispatcher
    )

    val job = launch(dispatcher) {
        differ.submitData(pagingData)
    }

    repeat(10) { yield() }

    val items = differ.snapshot().items
    job.cancel()
    items
}
