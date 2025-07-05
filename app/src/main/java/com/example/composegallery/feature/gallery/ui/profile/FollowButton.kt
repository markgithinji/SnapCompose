package com.example.composegallery.feature.gallery.ui.profile

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.composegallery.R
import com.example.composegallery.feature.gallery.ui.common.ProgressIndicator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun FollowButton() {
    var isFollowing by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val followText = stringResource(id = R.string.follow)
    val followingText = stringResource(id = R.string.following)
    val buttonText = if (isFollowing) followingText else followText
    val backgroundColor by animateColorAsState(
        targetValue = if (isFollowing) Color(0xFFB2DFDB) else Color(0xFFFF6E6E),
        label = "buttonColor"
    )

    Button(
        onClick = {
            scope.launch {
                isLoading = true
                delay(600) // simulate network delay
                isFollowing = !isFollowing
                isLoading = false
            }
        },
        modifier = Modifier
            .height(36.dp)
            .defaultMinSize(minWidth = 96.dp),
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(50)
    ) {
        if (isLoading) {
            ProgressIndicator()
        }

        Text(
            buttonText,
            style = MaterialTheme.typography.labelMedium,
            color = Color.White
        )
    }
}