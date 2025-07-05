package com.example.composegallery.feature.gallery.ui.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.composegallery.R
import com.example.composegallery.feature.gallery.ui.common.PhotoImage

@Composable
fun ProfilePhotoCard(
    imageUrl: String,
    modifier: Modifier = Modifier,
    blurHash: String? = null,
    onRetry: () -> Unit,
    onClick: (() -> Unit)? = null
) {
    val clickableModifier = modifier
        .then(
            if (onClick != null) Modifier.clickable { onClick() } else Modifier
        )
        .padding(8.dp)
    val contentDescription = stringResource(id = R.string.user_photo)

    PhotoImage(
        imageUrl = imageUrl,
        contentDescription = contentDescription,
        modifier = clickableModifier,
        blurHash = blurHash,
        onRetry = onRetry
    )
}