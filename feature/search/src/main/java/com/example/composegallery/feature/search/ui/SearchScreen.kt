package com.example.composegallery.feature.search.ui

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import com.example.composegallery.core.ui.R
import com.example.composegallery.core.domain.model.Photo
import com.example.composegallery.core.ui.BottomLoadingIndicator
import com.example.composegallery.core.ui.InfoMessageScreen
import com.example.composegallery.core.ui.LoadMoreListError
import com.example.composegallery.core.ui.PhotoCard
import com.example.composegallery.core.ui.ProgressIndicator
import com.example.composegallery.core.ui.RetryButton
import com.example.composegallery.core.ui.SharedTransitionKeys
import com.example.composegallery.core.ui.calculateResponsiveColumnCount
import com.example.composegallery.core.domain.model.OrderBy
import com.example.composegallery.core.domain.model.RecentSearch
import com.example.composegallery.core.ui.theme.searchBar
import com.valentinilk.shimmer.shimmer

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SearchScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedContentScope,
    onBack: () -> Unit,
    onPhotoClick: (Photo) -> Unit,
    onShowSnackbar: (String, String?, SnackbarDuration) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val filters by viewModel.filters.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isSearching by viewModel.isSearching.collectAsStateWithLifecycle()
    var firstSearchDone by rememberSaveable { mutableStateOf(false) }
    val retryKeys = remember { mutableStateMapOf<String, Int>() }
    val pagedPhotos = viewModel.searchResults.collectAsLazyPagingItems()
    var showFilters by remember { mutableStateOf(false) }
    val recentSearches by viewModel.recentSearches.collectAsStateWithLifecycle()
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
    var isFocused by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is SearchUiEvent.ShowSnackbar -> {
                    onShowSnackbar(event.message, event.actionLabel, event.duration)
                }
            }
        }
    }

    LaunchedEffect(isOnline) {
        if (isOnline && pagedPhotos.loadState.refresh is LoadState.Error) {
            pagedPhotos.retry()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            val showRecentSearches = isFocused && filters.query.isEmpty() && recentSearches.isNotEmpty()

            if (showRecentSearches) {
                RecentSearchesList(
                    recentSearches = recentSearches,
                    onSearchClick = { query ->
                        viewModel.updateQuery(query)
                        viewModel.submitSearch(query)
                        firstSearchDone = true
                        focusManager.clearFocus()
                    },
                    onDeleteClick = { viewModel.deleteRecentSearch(it) },
                    onClearAllClick = { viewModel.clearRecentSearches() },
                    paddingValues = PaddingValues(top = 100.dp)
                )
            } else {
                SearchScreenContent(
                    showWelcome = filters.query.isEmpty(),
                    paddingValues = PaddingValues(top = 100.dp),
                    photos = pagedPhotos,
                    retryKeys = retryKeys,
                    onPhotoClick = { photo ->
                        focusManager.clearFocus()
                        onPhotoClick(photo)
                    },
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope,
                    isSearching = isSearching
                )
            }

            SearchScreenTopBar(
                query = searchQuery,
                activeFilters = filters.orientation != null || filters.color != null || filters.orderBy != OrderBy.RELEVANT,
                onQueryChange = { viewModel.updateQuery(it) },
                onFocusChange = { isFocused = it },
                onClearQuery = { viewModel.updateQuery("") },
                onSearchSubmit = {
                    val trimmed = searchQuery.trim()
                    if (trimmed.isNotEmpty()) {
                        viewModel.submitSearch(trimmed)
                        firstSearchDone = true
                    }
                    focusManager.clearFocus()
                },
                onFilterClick = { showFilters = true },
                onBack = {
                    if (isFocused) {
                        focusManager.clearFocus()
                    } else {
                        onBack()
                    }
                },
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
    onFocusChange: (Boolean) -> Unit,
    onClearQuery: () -> Unit,
    onSearchSubmit: () -> Unit,
    onFilterClick: () -> Unit,
    onBack: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedContentScope
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    val textFieldContentTransition = updateTransition(
        targetState = animatedVisibilityScope.transition.targetState,
        label = "text_field_content_transition"
    )

    val textFieldInnerContentAlpha by textFieldContentTransition.animateFloat(
        transitionSpec = {
            if (targetState == EnterExitState.Visible) {
                tween(durationMillis = 300, delayMillis = 100)
            } else {
                tween(durationMillis = 80)
            }
        }, label = "text_field_alpha"
    ) { state ->
        if (state == EnterExitState.Visible) 1f else 0f
    }

    with(sharedTransitionScope) {
        with(animatedVisibilityScope) {
            Row(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth()
                    .height(72.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .renderInSharedTransitionScopeOverlay(zIndexInOverlay = 3f)
                        .animateEnterExit(
                            enter = fadeIn(tween(300)),
                            exit = fadeOut(tween(80))
                        )
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }

                Spacer(modifier = Modifier.width(8.dp))

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
                            .alpha(textFieldInnerContentAlpha)
                            .onFocusChanged { onFocusChange(it.isFocused) },
                        trailingIcon = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (query.isNotEmpty()) {
                                    IconButton(onClick = onClearQuery) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = stringResource(R.string.clear_search)
                                        )
                                    }
                                }
                                IconButton(onClick = onSearchSubmit) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = stringResource(R.string.search_icon_description)
                                    )
                                }
                            }
                        },
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

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .renderInSharedTransitionScopeOverlay(zIndexInOverlay = 3f)
                        .animateEnterExit(
                            enter = fadeIn(tween(300)),
                            exit = fadeOut(tween(80))
                        )
                ) {
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
    animatedVisibilityScope: AnimatedContentScope,
    isSearching: Boolean
) {
    val loadState = photos.loadState
    val isRefreshLoading = loadState.refresh is LoadState.Loading
    val isRefreshError = loadState.refresh is LoadState.Error
    val isEmpty = loadState.refresh is LoadState.NotLoading && 
            loadState.refresh.endOfPaginationReached && 
            photos.itemCount == 0 &&
            !showWelcome

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        when {
            showWelcome -> {
                InfoMessageScreen(
                    imageRes = R.drawable.no_search_icon,
                    title = stringResource(R.string.over_6_million_photos),
                    subtitle = stringResource(R.string.search_suggestion)
                )
            }

            // Show loading if we are actively refreshing OR if we have a manual search trigger
            (isRefreshLoading || isSearching || (photos.itemCount == 0 && !isEmpty && !isRefreshError && !showWelcome)) -> {
                ProgressIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 100.dp)
                )
            }

            isRefreshError -> {
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

            isEmpty -> {
                InfoMessageScreen(
                    imageRes = R.drawable.no_results_icon,
                    title = stringResource(R.string.no_results_title),
                    subtitle = stringResource(R.string.no_results_subtitle)
                )
            }

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
                        key = { index -> 
                            val photo = photos.peek(index)
                            photo?.let { "search_${it.id}_$index" } ?: index
                        },
                        contentType = photos.itemContentType { "photo" }
                    ) { index ->
                        val photo = photos[index]
                        if (photo != null) {
                            val retryKey = retryKeys[photo.id] ?: 0
                            val url = if (retryKey > 0) "${photo.smallUrl}?retry=$retryKey" else photo.smallUrl

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
                                Unit
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun RecentSearchesList(
    recentSearches: List<RecentSearch>,
    onSearchClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    onClearAllClick: () -> Unit,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.recent_searches),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onClearAllClick) {
                    Text(text = stringResource(R.string.clear_all))
                }
            }
        }

        items(recentSearches, key = { it.query }) { search ->
            RecentSearchItem(
                query = search.query,
                onClick = { onSearchClick(search.query) },
                onDeleteClick = { onDeleteClick(search.query) }
            )
        }
    }
}

@Composable
private fun RecentSearchItem(
    query: String,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.History,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = query,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        IconButton(onClick = onDeleteClick) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.delete_search),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
