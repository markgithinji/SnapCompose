package com.example.composegallery.feature.gallery.ui.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.composegallery.feature.gallery.domain.model.Photo
import com.example.composegallery.feature.gallery.ui.common.PhotoViewerScreen
import com.example.composegallery.feature.gallery.ui.gallery.GalleryScreen
import com.example.composegallery.feature.gallery.ui.gallery.FavoritesScreen
import com.example.composegallery.feature.gallery.ui.photodetail.PhotoDetailScreen
import com.example.composegallery.feature.gallery.ui.profile.CollectionDetailScreen
import com.example.composegallery.feature.gallery.ui.profile.UserProfileScreen
import com.example.composegallery.feature.gallery.ui.search.SearchScreen

@OptIn(ExperimentalSharedTransitionApi::class)
fun NavGraphBuilder.galleryRoute(
    sharedTransitionScope: SharedTransitionScope,
    onSearchClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onPhotoClick: (Photo, String) -> Unit
) {
    composable<GalleryRoute> {
        GalleryScreen(
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = this,
            onSearchNavigate = onSearchClick,
            onFavoritesNavigate = onFavoritesClick,
            onPhotoClick = { photo -> onPhotoClick(photo, "gallery") }
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
fun NavGraphBuilder.favoritesRoute(
    sharedTransitionScope: SharedTransitionScope,
    onBack: () -> Unit,
    onPhotoClick: (Photo) -> Unit
) {
    composable<FavoritesRoute> {
        FavoritesScreen(
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = this,
            onBack = onBack,
            onPhotoClick = onPhotoClick
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
fun NavGraphBuilder.searchRoute(
    sharedTransitionScope: SharedTransitionScope,
    onBack: () -> Unit,
    onPhotoClick: (Photo, String) -> Unit
) {
    composable<SearchRoute> {
        SearchScreen(
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = this,
            onBack = onBack,
            onPhotoClick = { photo -> onPhotoClick(photo, "gallery") }
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
fun NavGraphBuilder.photoDetailRoute(
    sharedTransitionScope: SharedTransitionScope,
    navController: NavController
) {
    composable<PhotoDetailRoute> { backStackEntry ->
        val args = backStackEntry.toRoute<PhotoDetailRoute>()
        PhotoDetailScreen(
            photoId = args.photoId,
            initialWidth = args.width,
            initialHeight = args.height,
            initialThumbUrl = args.thumbUrl,
            initialBlurHash = args.blurHash,
            origin = args.origin,
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = this,
            onBack = { navController.popBackStack() },
            onExpandClick = { photoId ->
                navController.navigate(FullscreenPhotoRoute(photoId))
            },
            onUserClick = { user ->
                navController.navigate(
                    UserProfileRoute(
                        username = user.username ?: user.authorName,
                        name = user.authorName,
                        profileImageUrl = user.authorProfileImageHighResUrl
                    )
                )
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

@OptIn(ExperimentalSharedTransitionApi::class)
fun NavGraphBuilder.userProfileRoute(
    sharedTransitionScope: SharedTransitionScope,
    navController: NavController
) {
    composable<UserProfileRoute> { backStackEntry ->
        val args = backStackEntry.toRoute<UserProfileRoute>()
        UserProfileScreen(
            username = args.username,
            initialName = args.name,
            initialProfileImageUrl = args.profileImageUrl,
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = this,
            onBack = { navController.popBackStack() },
            onPhotoClick = { photo ->
                navController.navigate(
                    PhotoDetailRoute(
                        photoId = photo.id,
                        width = photo.width,
                        height = photo.height,
                        thumbUrl = photo.smallUrl,
                        blurHash = photo.blurHash,
                        origin = "profile_${args.username}"
                    )
                )
            },
            onCollectionClick = { collection ->
                navController.navigate(
                    CollectionDetailRoute(
                        collectionId = collection.id,
                        collectionTitle = collection.title,
                        totalPhotos = collection.totalPhotos
                    )
                )
            }
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
fun NavGraphBuilder.collectionDetailRoute(
    sharedTransitionScope: SharedTransitionScope,
    navController: NavController
) {
    composable<CollectionDetailRoute> { backStackEntry ->
        val args = backStackEntry.toRoute<CollectionDetailRoute>()
        CollectionDetailScreen(
            collectionId = args.collectionId,
            collectionTitle = args.collectionTitle,
            totalPhotos = args.totalPhotos,
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = this,
            onBack = { navController.popBackStack() },
            onPhotoClick = { photo ->
                navController.navigate(
                    PhotoDetailRoute(
                        photoId = photo.id,
                        width = photo.width,
                        height = photo.height,
                        thumbUrl = photo.smallUrl,
                        blurHash = photo.blurHash,
                        origin = "collection_${args.collectionId}"
                    )
                )
            }
        )
    }
}
