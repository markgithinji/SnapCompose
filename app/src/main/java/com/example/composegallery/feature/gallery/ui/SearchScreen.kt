package com.example.composegallery.feature.gallery.ui

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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SearchScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedContentScope,
    onBack: () -> Unit
) {
    var query by rememberSaveable { mutableStateOf("") }
    var submittedQuery by rememberSaveable { mutableStateOf<String?>(null) }
    val searchBarSharedKey = "searchBarElement"

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
        }, label = "text_field_inner_alpha"
    ) { state ->
        if (state == EnterExitState.Visible) 1f else 0f
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars),
        topBar = {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }

                Spacer(modifier = Modifier.width(8.dp))

                with(sharedTransitionScope) {
                    TextField(
                        value = query,
                        onValueChange = { query = it },
                        placeholder = {
                            Text(
                                "Search Unsplash...",
                                modifier = Modifier.alpha(textFieldInnerContentAlpha)
                            )
                        },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .sharedElement(
                                sharedContentState = rememberSharedContentState(key = searchBarSharedKey),
                                animatedVisibilityScope = animatedVisibilityScope
                            )
                            .renderInSharedTransitionScopeOverlay()
                            .alpha(textFieldInnerContentAlpha),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            disabledContainerColor = MaterialTheme.colorScheme.surface,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(8.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                submittedQuery = query.trim().takeIf { it.isNotEmpty() }
                            }
                        )
                    )
                }
            }
        }
    ) { padding ->
        with(animatedVisibilityScope) { // Apply animateEnterExit to the content area below the search bar
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .animateEnterExit(
                        enter = fadeIn(animationSpec = tween(delayMillis = 200)) + slideInVertically(
                            animationSpec = tween(delayMillis = 200),
                            initialOffsetY = { fullHeight -> fullHeight / 2 } // Starts from halfway down
                        ),
                        exit = fadeOut(animationSpec = tween(durationMillis = 150)) + slideOutVertically(
                            animationSpec = tween(durationMillis = 150),
                            targetOffsetY = { fullHeight -> fullHeight / 2 } // Slides out halfway down
                        )
                    )
            ) {
                if (submittedQuery == null) { // ie. if we haven't submitted a query yet
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Let's look for something beautiful",
                            style = MaterialTheme.typography.headlineMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(10) { index ->
                            ListItem(
                                headlineContent = {
                                    Text("Result #$index for \"${submittedQuery}\"")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}


