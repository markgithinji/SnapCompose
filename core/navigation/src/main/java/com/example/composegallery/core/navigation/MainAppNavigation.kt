package com.example.composegallery.core.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.composegallery.core.common.message.MessageDuration
import com.example.composegallery.core.domain.model.FavoritesRoute
import com.example.composegallery.core.domain.model.GalleryRoute
import com.example.composegallery.core.domain.model.PhotoDetailRoute
import com.example.composegallery.core.domain.model.SearchRoute
import com.example.composegallery.core.ui.R
import com.example.composegallery.core.ui.component.SnapToast
import com.example.composegallery.core.ui.theme.ComposeGalleryTheme

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MainAppNavigation(
    viewModel: MainViewModel = hiltViewModel()
) {
    ComposeGalleryTheme {
        SharedTransitionLayout {
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            val snackbarHostState = remember { SnackbarHostState() }
            val messages by viewModel.messageManager.messages.collectAsStateWithLifecycle()

            if (messages.isNotEmpty()) {
                val message = messages.first()
                LaunchedEffect(message) {
                    snackbarHostState.showSnackbar(
                        message = message.message,
                        actionLabel = message.actionLabel,
                        duration = when (message.duration) {
                            MessageDuration.Short -> SnackbarDuration.Short
                            MessageDuration.Long -> SnackbarDuration.Long
                            MessageDuration.Indefinite -> SnackbarDuration.Indefinite
                        }
                    )
                    viewModel.messageManager.messageShown(message.id)
                }
            }

            val topLevelDestinations = listOf(
                TopLevelDestination(
                    route = GalleryRoute,
                    selectedIcon = Icons.Default.Home,
                    unselectedIcon = Icons.Outlined.Home,
                    labelRes = R.string.gallery
                ),
                TopLevelDestination(
                    route = FavoritesRoute,
                    selectedIcon = Icons.Default.Favorite,
                    unselectedIcon = Icons.Outlined.FavoriteBorder,
                    labelRes = R.string.favorites
                )
            )

            val showBottomBar = topLevelDestinations.any { destination ->
                currentDestination?.hierarchy?.any { it.hasRoute(destination.route::class) } == true
            }

            Scaffold(
                snackbarHost = {
                    SnackbarHost(hostState = snackbarHostState) { data ->
                        SnapToast(snackbarData = data)
                    }
                },
                bottomBar = {
                    if (showBottomBar) {
                        Column(
                            modifier = Modifier.background(MaterialTheme.colorScheme.background)
                        ) {
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                                thickness = 0.5.dp
                            )
                            NavigationBar(
                                containerColor = Color.Transparent,
                                tonalElevation = 0.dp
                            ) {
                                topLevelDestinations.forEach { destination ->
                                    val selected = currentDestination?.hierarchy?.any {
                                        it.hasRoute(destination.route::class)
                                    } == true
                                    NavigationBarItem(
                                        selected = selected,
                                        onClick = {
                                            navController.navigate(destination.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        icon = {
                                            Icon(
                                                imageVector = if (selected) destination.selectedIcon else destination.unselectedIcon,
                                                contentDescription = stringResource(destination.labelRes)
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = stringResource(destination.labelRes),
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                                            )
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = MaterialTheme.colorScheme.primary,
                                            selectedTextColor = MaterialTheme.colorScheme.primary,
                                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                            indicatorColor = Color.Transparent
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = GalleryRoute,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        galleryRoute(
                            sharedTransitionScope = this@SharedTransitionLayout,
                            onSearchClick = { navController.navigate(SearchRoute) },
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
    }
}

private data class TopLevelDestination<T : Any>(
    val route: T,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val labelRes: Int
)
