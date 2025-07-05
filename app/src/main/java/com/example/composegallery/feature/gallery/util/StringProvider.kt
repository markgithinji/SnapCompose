package com.example.composegallery.feature.gallery.util

import androidx.annotation.StringRes

interface StringProvider {
    fun get(@StringRes resId: Int, vararg args: Any): String
}
