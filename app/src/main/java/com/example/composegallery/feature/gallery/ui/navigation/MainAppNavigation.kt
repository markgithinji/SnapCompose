package com.example.composegallery.feature.gallery.ui.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
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
                startDestination = GalleryRoute
            ) {
                galleryRoute(
                    sharedTransitionScope = this@SharedTransitionLayout,
                    onSearchClick = { navController.navigate(SearchRoute) },
                    onFavoritesClick = { navController.navigate(FavoritesRoute) },
                    onPhotoClick = { photo, origin ->
                        navController.navigate(
                            PhotoDetailRoute(
                                photoId = photo.id,
                                width = photo.width,
                                height = photo.height,
                                thumbUrl = photo.smallUrl,
                                blurHash = photo.blurHash,
                                origin = origin
                            )
                        )
                    }
                )

                searchRoute(
                    sharedTransitionScope = this@SharedTransitionLayout,
                    onBack = { navController.popBackStack() },
                    onPhotoClick = { photo, origin ->
                        navController.navigate(
                            PhotoDetailRoute(
                                photoId = photo.id,
                                width = photo.width,
                                height = photo.height,
                                thumbUrl = photo.smallUrl,
                                blurHash = photo.blurHash,
                                origin = origin
                            )
                        )
                    }
                )

                favoritesRoute(
                    sharedTransitionScope = this@SharedTransitionLayout,
                    onBack = { navController.popBackStack() },
                    onPhotoClick = { photo ->
                        navController.navigate(
                            PhotoDetailRoute(
                                photoId = photo.id,
                                width = photo.width,
                                height = photo.height,
                                thumbUrl = photo.smallUrl,
                                blurHash = photo.blurHash,
                                origin = "favorites"
                            )
                        )
                    }
                )

                photoDetailRoute(
                    sharedTransitionScope = this@SharedTransitionLayout,
                    navController = navController
                )
                fullscreenPhotoRoute()
                userProfileRoute(
                    sharedTransitionScope = this@SharedTransitionLayout,
                    navController = navController
                )
                collectionDetailRoute(
                    sharedTransitionScope = this@SharedTransitionLayout,
                    navController = navController
                )
            }
        }
    }
}
