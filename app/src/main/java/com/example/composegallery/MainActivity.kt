package com.example.composegallery

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.composegallery.feature.gallery.ui.GalleryScreen
import com.example.composegallery.feature.gallery.ui.GalleryViewModel
import com.example.composegallery.ui.theme.ComposeGalleryTheme
import timber.log.Timber

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<GalleryViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeGalleryTheme {
                Surface {
                    val images by viewModel.images.collectAsState()
                    GalleryScreen(images = images)
                }
            }
        }
    }
}