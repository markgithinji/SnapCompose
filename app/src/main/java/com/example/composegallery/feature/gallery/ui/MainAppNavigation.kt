package com.example.composegallery.feature.gallery.ui

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.composegallery.ui.theme.ComposeGalleryTheme

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MainAppNavigation() {
    ComposeGalleryTheme {
        SharedTransitionLayout {
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = GalleryRoute
            ) {
                composable<GalleryRoute> {
                    GalleryScreen(
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this,
                        onSearchNavigate = {
                            navController.navigate(SearchRoute)
                        },
                        onPhotoClick = { photoId ->
                            navController.navigate(PhotoDetailRoute(photoId))
                        }
                    )
                }
                composable<SearchRoute> {
                    SearchScreen(
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this,
                        onBack = { navController.popBackStack() },
                        onPhotoClick = { photoId ->
                            navController.navigate(PhotoDetailRoute(photoId))
                        }
                    )
                }
                composable<PhotoDetailRoute> { backStackEntry ->
                    val photoDetailRoute = backStackEntry.toRoute<PhotoDetailRoute>()
                    PhotoDetailScreen(
                        photoId = photoDetailRoute.photoId,
                        onBack = { navController.popBackStack() },
                        onExpandClick = { photoId ->
                            navController.navigate(FullscreenPhotoRoute(photoId))
                        },
                        onUserClick = { username ->
                            navController.navigate(UserProfileRoute(username))
                        }
                    )
                }
                composable<FullscreenPhotoRoute> { backStackEntry ->
                    val fullscreenPhotoRoute = backStackEntry.toRoute<FullscreenPhotoRoute>()
                    PhotoViewerScreen(
                        photoId = fullscreenPhotoRoute.photoId
                    )
                }
                composable<UserProfileRoute> { backStackEntry ->
                    val userProfileRoute = backStackEntry.toRoute<UserProfileRoute>()
                    UserProfileScreen(
                        username = userProfileRoute.username,
                        onBack = { navController.popBackStack() },
                        onPhotoClick = { photoId ->
                            navController.navigate(PhotoDetailRoute(photoId))
                        }
                    )
                }
            }
        }
    }
}