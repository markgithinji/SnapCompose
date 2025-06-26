package com.example.composegallery

import android.content.ComponentCallbacks2
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.composegallery.feature.gallery.ui.util.BlurHashDecoder
import com.example.composegallery.feature.gallery.ui.GalleryScreen
import com.example.composegallery.ui.theme.ComposeGalleryTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ComposeGalleryTheme {
                GalleryScreen()
            }
        }
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)

        if (level >= ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            BlurHashDecoder.clearCache()
        }
    }
}