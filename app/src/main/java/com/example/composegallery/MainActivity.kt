package com.example.composegallery

import android.content.ComponentCallbacks2
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.composegallery.feature.gallery.ui.GalleryScreen
import com.example.composegallery.feature.gallery.ui.SearchScreen
import com.example.composegallery.feature.gallery.ui.util.BlurHashDecoder
import com.example.composegallery.ui.theme.ComposeGalleryTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ComposeGalleryTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "gallery"
                ) {
                    composable("gallery") {
                        GalleryScreen(
                            onSearchNavigate = {
                                navController.navigate("search")
                            }
                        )
                    }

                    composable("search") {
                        SearchScreen(onBack = { navController.popBackStack() })
                    }
                }
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
