package com.example.composegallery.feature.home.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.composegallery.core.common.Result as AppResult
import com.example.composegallery.core.domain.repository.GalleryRepository
import com.example.composegallery.core.network.paging.PagingDefaults
import com.example.composegallery.feature.home.data.PhotoRemoteMediator
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

@HiltWorker
class EditorialPreFetchWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: GalleryRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        Timber.d("Starting daily editorial pre-fetch")
        return try {
            val syncResult = repository.syncPhotos(
                topicId = PhotoRemoteMediator.EDITORIAL,
                page = 1,
                pageSize = PagingDefaults.PAGE_SIZE,
                isRefresh = true
            )
            if (syncResult is AppResult.Success) {
                Timber.d("Editorial pre-fetch successful")
                Result.success()
            } else {
                Timber.e("Editorial pre-fetch failed")
                Result.retry()
            }
        } catch (e: Exception) {
            Timber.e(e, "Error during editorial pre-fetch")
            Result.failure()
        }
    }

    companion object {
        const val WORK_NAME = "EditorialPreFetchWork"
    }
}
