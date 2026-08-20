package com.example.composegallery.feature.gallery.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BottomLoadingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp) // Fixed height to prevent layout jumps
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        ProgressIndicator()
    }
}
