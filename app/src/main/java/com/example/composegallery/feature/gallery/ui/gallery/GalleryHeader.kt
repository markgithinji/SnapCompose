package com.example.composegallery.feature.gallery.ui.gallery

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.example.composegallery.ui.theme.searchBar


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun GalleryHeader(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        Text(
            text = "Snap",
            style = MaterialTheme.typography.displayLarge
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Beautiful, free photos.",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        DummySearchBar(
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = animatedVisibilityScope,
            onClick = onSearchClick
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun DummySearchBar(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val searchBarSharedKey = "searchBarElement"

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
                    tween(durationMillis = 0) // When going back to GalleryScreen (this composable becomes visible), appear instantly
                }
            },
            label = "dummy_search_bar_alpha"
        ) { state ->
            if (state != EnterExitState.Visible) 0f else 1f // Alpha 0 when exiting, 1 otherwise
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .sharedElement(
                    sharedContentState = rememberSharedContentState(key = searchBarSharedKey),
                    animatedVisibilityScope = animatedVisibilityScope
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                ),
            shape = MaterialTheme.shapes.searchBar,
            color = MaterialTheme.colorScheme.surfaceVariant
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
                    text = "Search photos...",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            }
        }
    }
}


