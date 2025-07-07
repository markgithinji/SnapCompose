package com.example.composegallery.feature.gallery.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.composegallery.R
import com.example.composegallery.ui.theme.ComposeGalleryTheme

@Composable
fun PhotoCard(
    imageUrl: String,
    authorName: String,
    authorImageUrl: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    blurHash: String? = null,
    onClick: (() -> Unit)? = null,
) {
    val clickableModifier = Modifier
        .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
        .padding(8.dp)

    Column(modifier = clickableModifier) {
        PhotoImage(
            imageUrl = imageUrl,
            contentDescription = authorName,
            modifier = modifier,
            blurHash = blurHash,
            onRetry = onRetry
        )

        Spacer(modifier = Modifier.height(4.dp))

        AuthorInfoRow(authorName = authorName, authorImageUrl = authorImageUrl)
    }
}

@Composable
fun AuthorInfoRow(authorName: String, authorImageUrl: String) {
    val contentDesc = stringResource(id = R.string.profile_picture_desc, authorName)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 4.dp)
    ) {
        AsyncImage(
            model = authorImageUrl,
            contentDescription = contentDesc,
            modifier = Modifier
                .size(25.dp)
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = authorName, style = MaterialTheme.typography.labelLarge
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPhotoCard() {
    val dummyPhotoUrl = "https://images.unsplash.com/photo-1503023345310-bd7c1de61c7d"
    val dummyAuthorName = "Jane Doe"
    val dummyAuthorAvatarUrl = "https://randomuser.me/api/portraits/women/44.jpg"
    val dummyBlurHash = "LKO2?U%2Tw=w]~RBVZRi};RPxuwH"

    ComposeGalleryTheme {
        Surface {
            PhotoCard(
                imageUrl = dummyPhotoUrl,
                authorName = dummyAuthorName,
                authorImageUrl = dummyAuthorAvatarUrl,
                blurHash = dummyBlurHash,
                modifier = Modifier.fillMaxWidth(),
                onClick = {}, // no-op
                onRetry = {}  // no-op
            )
        }
    }
}
