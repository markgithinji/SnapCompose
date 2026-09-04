package com.example.composegallery.feature.gallery.ui.gallery

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.composegallery.R
import com.example.composegallery.feature.gallery.domain.model.Topic
import com.example.composegallery.feature.gallery.ui.common.SharedTransitionKeys
import com.example.composegallery.feature.gallery.ui.util.UiState
import com.example.composegallery.ui.theme.searchBar
import com.valentinilk.shimmer.shimmer

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun GalleryHeader(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    topicsState: UiState<List<Topic>>,
    selectedTopicId: String?,
    onTopicSelected: (String?) -> Unit,
    onSearchClick: () -> Unit,
    onRetryTopics: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.app_title),
                style = MaterialTheme.typography.displayLarge
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = stringResource(R.string.app_tagline),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f),
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        DummySearchBar(
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = animatedVisibilityScope,
            onClick = onSearchClick,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        TopicTabs(
            state = topicsState,
            selectedTopicId = selectedTopicId,
            onTopicSelected = onTopicSelected,
            onRetry = onRetryTopics
        )
    }
}

@Composable
private fun TopicTabs(
    state: UiState<List<Topic>>,
    selectedTopicId: String?,
    onTopicSelected: (String?) -> Unit,
    onRetry: () -> Unit
) {
    if (state is UiState.Error) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                onClick = onRetry,
                shape = CircleShape,
                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f),
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.failed_to_load_topics),
                        style = MaterialTheme.typography.labelMedium
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "↺",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    } else {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (state) {
                is UiState.Loading -> {
                    items(6) {
                        Box(
                            modifier = Modifier
                                .width(80.dp)
                                .height(36.dp)
                                .clip(CircleShape)
                                .shimmer()
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                        )
                    }
                }

                is UiState.Content -> {
                    val topics = state.data
                    val allTabs = listOf(null) + topics.map { it.id }
                    items(allTabs, key = { it ?: "editorial" }) { topicId ->
                        val isSelected = topicId == selectedTopicId
                        val title = if (topicId == null) {
                            stringResource(R.string.editorial)
                        } else {
                            topics.find { it.id == topicId }?.title ?: ""
                        }

                        Surface(
                            onClick = { onTopicSelected(topicId) },
                            shape = CircleShape,
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            },
                            contentColor = if (isSelected) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        ) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun DummySearchBar(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    with(sharedTransitionScope) {

        val contentTransition = updateTransition(
            targetState = animatedVisibilityScope.transition.targetState,
            label = "dummy_bar_content_transition"
        )

        val contentAlpha by contentTransition.animateFloat(
            transitionSpec = {
                if (targetState != EnterExitState.Visible) {
                    tween(durationMillis = 150) // Fade out quickly when this composable is going away
                } else {
                    tween(durationMillis = 300, delayMillis = 100)
                }
            },
            label = "dummy_search_bar_alpha"
        ) { state ->
            if (state != EnterExitState.Visible) 0f else 1f // Alpha 0 when exiting, 1 otherwise
        }

        Surface(
            modifier = modifier
                .fillMaxWidth()
                .height(56.dp) // Match the real search bar height for a smoother transition
                .sharedElement(
                    sharedContentState = rememberSharedContentState(key = SharedTransitionKeys.SEARCH_BAR),
                    animatedVisibilityScope = animatedVisibilityScope
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                ),
            shape = MaterialTheme.shapes.searchBar,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .alpha(contentAlpha),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.search_hint),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(R.string.search_icon_description)
                )
            }
        }
    }
}


