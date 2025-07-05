package com.example.composegallery.feature.gallery.util

import android.content.Context
import androidx.annotation.StringRes
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class DefaultStringProvider @Inject constructor(
    @ApplicationContext private val context: Context
) : StringProvider {

    override fun get(@StringRes resId: Int, vararg args: Any): String {
        return context.getString(resId, *args)
    }
}
