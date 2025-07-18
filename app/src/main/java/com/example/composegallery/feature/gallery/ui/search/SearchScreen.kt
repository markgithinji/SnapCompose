package com.example.composegallery.feature.gallery.ui.search

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.composegallery.R
import com.example.composegallery.feature.gallery.domain.model.Photo
import com.example.composegallery.feature.gallery.ui.common.InfoMessageScreen
import com.example.composegallery.feature.gallery.ui.common.LoadMoreListError
import com.example.composegallery.feature.gallery.ui.common.PhotoCard
import com.example.composegallery.feature.gallery.ui.common.ProgressIndicator
import com.example.composegallery.feature.gallery.ui.common.RetryButton
import com.example.composegallery.feature.gallery.ui.common.SharedTransitionKeys
import com.example.composegallery.feature.gallery.ui.common.calculateResponsiveColumnCount
import com.example.composegallery.ui.theme.searchBar

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SearchScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedContentScope,
    onBack: () -> Unit,
    onPhotoClick: (String) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    var query by rememberSaveable { mutableStateOf("") }
    var firstSearchDone by rememberSaveable { mutableStateOf(false) }
    val retryKeys = remember { mutableStateMapOf<String, Int>() }
    val pagedPhotos = viewModel.searchResults.collectAsLazyPagingItems()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars),
        topBar = {
            SearchScreenTopBar(
                query = query,
                onQueryChange = { query = it },
                onSearchSubmit = {
                    val trimmed = query.trim()
                    if (trimmed.isNotEmpty()) {
                        viewModel.submitSearch(trimmed)
                        firstSearchDone = true
                    }
                },
                onBack = onBack,
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope
            )
        }
    ) { padding ->
        SearchScreenContent(
            showWelcome = !firstSearchDone,
            paddingValues = padding,
            photos = pagedPhotos,
            retryKeys = retryKeys,
            onPhotoClick = onPhotoClick,
            animatedVisibilityScope = animatedVisibilityScope
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SearchScreenTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearchSubmit: () -> Unit,
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
                tween(durationMillis = 200, delayMillis = 100)
            } else {
                tween(durationMillis = 0)
            }
        }, label = "text_field_alpha"
    ) { state ->
        if (state == EnterExitState.Visible) 1f else 0f
    }

    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }

        Spacer(modifier = Modifier.width(8.dp))

        with(sharedTransitionScope) {
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
                    .weight(1f)
                    .height(56.dp)
                    .sharedElement(
                        sharedContentState = rememberSharedContentState(key = SharedTransitionKeys.SEARCH_BAR),
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                    .alpha(textFieldInnerContentAlpha),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = MaterialTheme.shapes.searchBar,
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
}

@Composable
private fun SearchScreenContent(
    showWelcome: Boolean,
    paddingValues: PaddingValues,
    photos: LazyPagingItems<Photo>,
    retryKeys: SnapshotStateMap<String, Int>,
    onPhotoClick: (String) -> Unit,
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
                    ProgressIndicator()
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
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(calculateResponsiveColumnCount()),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            count = photos.itemCount,
                            key = { index ->
                                val item = photos.peek(index)
                                item?.id ?: index
                            }
                        ) { index ->
                            val photo = photos[index]
                            if (photo != null) {
                                val retryKey = retryKeys[photo.id] ?: 0
                                val url =
                                    if (retryKey > 0) "${photo.fullUrl}?retry=$retryKey" else photo.fullUrl

                                PhotoCard(
                                    imageUrl = url,
                                    authorName = photo.authorName,
                                    authorImageUrl = "${photo.authorProfileImageMediumResUrl}?retry=$retryKey",
                                    onRetry = { retryKeys[photo.id] = retryKey + 1 },
                                    blurHash = photo.blurHash,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(photo.width.toFloat() / photo.height)
                                        .clip(RoundedCornerShape(12.dp)),
                                    onClick = { onPhotoClick(photo.id) }
                                )
                            }
                        }

                        // Handle append loading/error states (pagination)
                        item(span = { GridItemSpan(maxCurrentLineSpan) }) {
                            when (loadState.append) {
                                is LoadState.Loading -> {
                                    ProgressIndicator()
                                }

                                is LoadState.Error -> {
                                    val error = (loadState.append as LoadState.Error).error
                                    LoadMoreListError(
                                        message = error.localizedMessage
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

