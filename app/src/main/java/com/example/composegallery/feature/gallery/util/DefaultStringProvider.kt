package com.example.composegallery.feature.gallery.util

import android.content.Context
import androidx.annotation.StringRes
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * A default implementation of [StringProvider] that retrieves strings
 * from Android resources using the application [Context].
 *
 * This class is typically injected using a dependency injection framework like Hilt.
 *
 * @property context The application [Context] used to access string resources.
 *                   This is injected via `@ApplicationContext`.
 */
class DefaultStringProvider @Inject constructor(
    @ApplicationContext private val context: Context
) : StringProvider {

    override fun get(@StringRes resId: Int, vararg args: Any): String {
        return context.getString(resId, *args)
    }
}
