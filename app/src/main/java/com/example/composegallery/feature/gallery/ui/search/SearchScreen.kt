package com.example.composegallery.feature.gallery.ui.search

import androidx.compose.animation.AnimatedContentScope
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import androidx.paging.compose.itemContentType
import com.example.composegallery.R
import com.example.composegallery.feature.gallery.domain.model.OrderBy
import com.example.composegallery.feature.gallery.domain.model.Photo
import com.example.composegallery.feature.gallery.domain.model.SearchFilters
import com.example.composegallery.feature.gallery.ui.common.BottomLoadingIndicator
import com.example.composegallery.feature.gallery.ui.common.InfoMessageScreen
import com.example.composegallery.feature.gallery.ui.common.LoadMoreListError
import com.example.composegallery.feature.gallery.ui.common.PhotoCard
import com.example.composegallery.feature.gallery.ui.common.ProgressIndicator
import com.example.composegallery.feature.gallery.ui.common.RetryButton
import com.example.composegallery.feature.gallery.ui.common.SharedTransitionKeys
import com.example.composegallery.feature.gallery.ui.common.calculateResponsiveColumnCount
import com.example.composegallery.ui.theme.searchBar
import com.valentinilk.shimmer.shimmer

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SearchScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedContentScope,
    onBack: () -> Unit,
    onPhotoClick: (Photo) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val filters by viewModel.filters.collectAsStateWithLifecycle()
    var firstSearchDone by rememberSaveable { mutableStateOf(false) }
    val retryKeys = remember { mutableStateMapOf<String, Int>() }
    val pagedPhotos = viewModel.searchResults.collectAsLazyPagingItems()
    var showFilters by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            SearchScreenContent(
                showWelcome = !firstSearchDone,
                paddingValues = PaddingValues(top = 100.dp), // Fixed space for the floating top bar
                photos = pagedPhotos,
                retryKeys = retryKeys,
                onPhotoClick = onPhotoClick,
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope
            )

            SearchScreenTopBar(
                query = filters.query,
                activeFilters = filters.orientation != null || filters.color != null || filters.orderBy != OrderBy.RELEVANT,
                onQueryChange = { viewModel.updateQuery(it) },
                onSearchSubmit = {
                    val trimmed = filters.query.trim()
                    if (trimmed.isNotEmpty()) {
                        viewModel.submitSearch(trimmed)
                        firstSearchDone = true
                    }
                },
                onFilterClick = { showFilters = true },
                onBack = onBack,
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope
            )
        }
    }

    if (showFilters) {
        SearchFilterBottomSheet(
            filters = filters,
            onDismissRequest = { showFilters = false },
            onApplyFilters = { newFilters ->
                viewModel.applyFilters(newFilters)
                if (newFilters.query.isNotBlank()) {
                    firstSearchDone = true
                }
            }
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SearchScreenTopBar(
    query: String,
    activeFilters: Boolean,
    onQueryChange: (String) -> Unit,
    onSearchSubmit: () -> Unit,
    onFilterClick: () -> Unit,
    onBack: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedContentScope
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    // A transition to manage the alpha of the TextField's placeholder and text
    val textFieldContentTransition = updateTransition(
        targetState = animatedVisibilityScope.transition.targetState,
        label = "text_field_content_transition"
    )

    val textFieldInnerContentAlpha by textFieldContentTransition.animateFloat(
        transitionSpec = {
            if (targetState == EnterExitState.Visible) {
                tween(durationMillis = 300, delayMillis = 100)
            } else {
                tween(durationMillis = 150) // Smooth fade out on return
            }
        }, label = "text_field_alpha"
    ) { state ->
        if (state == EnterExitState.Visible) 1f else 0f
    }

    Row(
        modifier = Modifier
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .height(72.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val controlsAlpha by animatedVisibilityScope.transition.animateFloat(
            transitionSpec = { 
                if (targetState == EnterExitState.Visible) tween(300) 
                else tween(150) 
            },
            label = "controls_alpha"
        ) { state ->
            if (state == EnterExitState.Visible) 1f else 0f
        }

        IconButton(
            onClick = onBack,
            modifier = Modifier.alpha(controlsAlpha)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }

        Spacer(modifier = Modifier.width(8.dp))

        with(sharedTransitionScope) {
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .sharedElement(
                        sharedContentState = rememberSharedContentState(key = SharedTransitionKeys.SEARCH_BAR),
                        animatedVisibilityScope = animatedVisibilityScope
                    ),
                shape = MaterialTheme.shapes.searchBar,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                TextField(
                    value = query,
                    onValueChange = onQueryChange,
                    textStyle = MaterialTheme.typography.headlineSmall,
                    placeholder = {
                        Text(
                            text = stringResource(R.string.search_unsplash),
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                            modifier = Modifier.alpha(textFieldInnerContentAlpha)
                        )
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(textFieldInnerContentAlpha),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            keyboardController?.hide()
                            onSearchSubmit()
                        }
                    )
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Box(modifier = Modifier.alpha(controlsAlpha)) {
            IconButton(onClick = onFilterClick) {
                Icon(
                    Icons.Default.Tune,
                    contentDescription = stringResource(R.string.filters),
                    tint = if (activeFilters) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            }
            if (activeFilters) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .align(Alignment.TopEnd)
                        .padding(2.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SearchScreenContent(
    showWelcome: Boolean,
    paddingValues: PaddingValues,
    photos: LazyPagingItems<Photo>,
    retryKeys: SnapshotStateMap<String, Int>,
    onPhotoClick: (Photo) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedContentScope
) {
    val loadState = photos.loadState
    val isEmpty = photos.itemCount == 0 && loadState.refresh is LoadState.NotLoading

    with(animatedVisibilityScope) { // Apply animateEnterExit to the content area below the search bar
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .animateEnterExit(
                    enter = fadeIn(animationSpec = tween(delayMillis = 200)) + slideInVertically(
                        animationSpec = tween(delayMillis = 200),
                        initialOffsetY = { it / 2 } // Start from halfway down
                    ),
                    exit = fadeOut(animationSpec = tween(150)) + slideOutVertically(
                        animationSpec = tween(150),
                        targetOffsetY = { it / 2 } // Slide out halfway down
                    )
                )
        ) {
            when {

                showWelcome -> { // ie. if we haven't submitted a query yet
                    InfoMessageScreen(
                        imageRes = R.drawable.no_search_icon,
                        title = stringResource(R.string.over_6_million_photos),
                        subtitle = stringResource(R.string.search_suggestion)
                    )
                }

                // Initial loading state (first page)
                loadState.refresh is LoadState.Loading -> {
                    ProgressIndicator(modifier = Modifier.fillMaxSize())
                }

                // Error on first page
                loadState.refresh is LoadState.Error -> {
                    val error = (loadState.refresh as LoadState.Error).error
                    InfoMessageScreen(
                        imageRes = R.drawable.error_icon,
                        title = stringResource(R.string.search_error_title),
                        subtitle = stringResource(
                            R.string.search_error_subtitle,
                            error.localizedMessage ?: stringResource(R.string.unknown_error)
                        ),
                        titleColor = MaterialTheme.colorScheme.error,
                        content = {
                            RetryButton(
                                onClick = { photos.retry() },
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    )
                }

                // No results after loading
                isEmpty -> {
                    InfoMessageScreen(
                        imageRes = R.drawable.no_results_icon,
                        title = stringResource(R.string.no_results_title),
                        subtitle = stringResource(R.string.no_results_subtitle)
                    )
                }

                // Content successfully loaded
                else -> {
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Fixed(calculateResponsiveColumnCount()),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 80.dp),
                        verticalItemSpacing = 12.dp,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            count = photos.itemCount,
                            key = photos.itemKey { it.id },
                            contentType = photos.itemContentType { "photo" }
                        ) { index ->
                            val photo = photos[index]
                            if (photo != null) {
                                val retryKey = retryKeys[photo.id] ?: 0
                                val url =
                                    if (retryKey > 0) "${photo.smallUrl}?retry=$retryKey" else photo.smallUrl

                                PhotoCard(
                                    imageUrl = url,
                                    authorName = photo.authorName,
                                    authorImageUrl = "${photo.authorProfileImageMediumResUrl}?retry=$retryKey",
                                    onRetry = { retryKeys[photo.id] = retryKey + 1 },
                                    sharedTransitionScope = sharedTransitionScope,
                                    animatedVisibilityScope = animatedVisibilityScope,
                                    photoId = photo.id,
                                    origin = "search",
                                    aspectRatio = photo.width.toFloat() / photo.height,
                                    blurHash = photo.blurHash,
                                    modifier = Modifier.fillMaxWidth(),
                                    onClick = { onPhotoClick(photo) }
                                )
                            } else {
                                // Placeholder
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1f)
                                        .padding(8.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .shimmer()
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                )
                            }
                        }

                        // Handle append loading/error states (pagination)
                        item(span = StaggeredGridItemSpan.FullLine) {
                            when (val loadStateAppend = loadState.append) {
                                is LoadState.Loading -> {
                                    BottomLoadingIndicator()
                                }

                                is LoadState.Error -> {
                                    LoadMoreListError(
                                        message = loadStateAppend.error.localizedMessage
                                            ?: stringResource(R.string.failed_to_load_more),
                                        onRetry = { photos.retry() }
                                    )
                                }

                                else -> {
                                    Unit // No-Op
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

