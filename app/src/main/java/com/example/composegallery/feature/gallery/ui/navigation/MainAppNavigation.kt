package com.example.composegallery.feature.gallery.ui.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.composegallery.feature.gallery.ui.profile.CollectionDetailScreen
import com.example.composegallery.feature.gallery.ui.gallery.GalleryScreen
import com.example.composegallery.feature.gallery.ui.photodetail.PhotoDetailScreen
import com.example.composegallery.feature.gallery.ui.common.PhotoViewerScreen
import com.example.composegallery.feature.gallery.ui.profile.UserProfileScreen
import com.example.composegallery.feature.gallery.ui.search.SearchScreen
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
                        },
                        onCollectionClick = { collectionId, title ->
                            navController.navigate(CollectionDetailRoute(collectionId, title))
                        }
                    )
                }
                composable<CollectionDetailRoute> { backStackEntry ->
                    val params = backStackEntry.toRoute<CollectionDetailRoute>()
                    CollectionDetailScreen(
                        collectionId = params.collectionId,
                        collectionTitle = params.collectionTitle,
                        onBack = { navController.popBackStack() },
                        onPhotoClick = { photoId -> navController.navigate(PhotoDetailRoute(photoId)) }
                    )
                }
            }
        }
    }
}