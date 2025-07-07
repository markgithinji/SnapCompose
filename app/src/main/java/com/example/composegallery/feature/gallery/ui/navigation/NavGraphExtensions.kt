package com.example.composegallery.feature.gallery.ui.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.composegallery.feature.gallery.ui.common.PhotoViewerScreen
import com.example.composegallery.feature.gallery.ui.gallery.GalleryScreen
import com.example.composegallery.feature.gallery.ui.photodetail.PhotoDetailScreen
import com.example.composegallery.feature.gallery.ui.profile.CollectionDetailScreen
import com.example.composegallery.feature.gallery.ui.profile.UserProfileScreen
import com.example.composegallery.feature.gallery.ui.search.SearchScreen

@OptIn(ExperimentalSharedTransitionApi::class)
fun NavGraphBuilder.galleryRoute(
    sharedTransitionScope: SharedTransitionScope,
    onSearchClick: () -> Unit,
    onPhotoClick: (String) -> Unit
) {
    composable<GalleryRoute> {
        GalleryScreen(
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = this,
            onSearchNavigate = onSearchClick,
            onPhotoClick = onPhotoClick
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
fun NavGraphBuilder.searchRoute(
    sharedTransitionScope: SharedTransitionScope,
    onBack: () -> Unit,
    onPhotoClick: (String) -> Unit
) {
    composable<SearchRoute> {
        SearchScreen(
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = this,
            onBack = onBack,
            onPhotoClick = onPhotoClick
        )
    }
}

fun NavGraphBuilder.photoDetailRoute(
    navController: NavController
) {
    composable<PhotoDetailRoute> { backStackEntry ->
        val args = backStackEntry.toRoute<PhotoDetailRoute>()
        PhotoDetailScreen(
            photoId = args.photoId,
            onBack = { navController.popBackStack() },
            onExpandClick = { photoId ->
                navController.navigate(FullscreenPhotoRoute(photoId))
            },
            onUserClick = { username ->
                navController.navigate(UserProfileRoute(username = username))
            }
        )
    }
}

fun NavGraphBuilder.fullscreenPhotoRoute() {
    composable<FullscreenPhotoRoute> { backStackEntry ->
        val args = backStackEntry.toRoute<FullscreenPhotoRoute>()
        PhotoViewerScreen(photoId = args.photoId)
    }
}

fun NavGraphBuilder.userProfileRoute(navController: NavController) {
    composable<UserProfileRoute> { backStackEntry ->
        val args = backStackEntry.toRoute<UserProfileRoute>()
        UserProfileScreen(
            username = args.username,
            onBack = { navController.popBackStack() },
            onPhotoClick = { photoId ->
                navController.navigate(PhotoDetailRoute(photoId))
            },
            onCollectionClick = { collectionId, title, totalPhotos ->
                navController.navigate(
                    CollectionDetailRoute(
                        collectionId = collectionId,
                        collectionTitle = title,
                        totalPhotos = totalPhotos
                    )
                )
            }
        )
    }
}

fun NavGraphBuilder.collectionDetailRoute(navController: NavController) {
    composable<CollectionDetailRoute> { backStackEntry ->
        val args = backStackEntry.toRoute<CollectionDetailRoute>()
        CollectionDetailScreen(
            collectionId = args.collectionId,
            collectionTitle = args.collectionTitle,
            totalPhotos = args.totalPhotos,
            onBack = { navController.popBackStack() },
            onPhotoClick = { photoId ->
                navController.navigate(PhotoDetailRoute(photoId))
            }
        )
    }
}
