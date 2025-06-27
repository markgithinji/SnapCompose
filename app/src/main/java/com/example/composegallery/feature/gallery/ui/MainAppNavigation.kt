package com.example.composegallery.feature.gallery.ui

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.composegallery.ui.theme.ComposeGalleryTheme

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MainAppNavigation() {
    ComposeGalleryTheme {
        SharedTransitionLayout {
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = Gallery
            ) {
                composable<Gallery> {
                    GalleryScreen(
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this,
                        onSearchNavigate = {
                            navController.navigate(Search)
                        }
                    )
                }

                composable<Search> {
                    SearchScreen(
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}