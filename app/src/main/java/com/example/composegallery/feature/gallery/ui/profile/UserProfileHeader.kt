package com.example.composegallery.feature.gallery.ui.profile

import ConfettiButton
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composegallery.R
import com.example.composegallery.feature.gallery.ui.common.UserProfileImage
import java.net.URI

@Composable
fun UserProfileHeader(
    name: String,
    profileImage: String,
    bio: String?,
    location: String?,
    portfolioUrl: String?,
    instagramUsername: String?,
    unsplashProfileUrl: String,
    modifier: Modifier = Modifier
) {
    var isBioExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Start
    ) {
        // 1. Profile Image
        UserProfileImage(
            imageUrl = profileImage,
            contentDescription = stringResource(R.string.profile_picture_desc, name),
            modifier = Modifier.size(140.dp)
        )

        Spacer(modifier = Modifier.width(24.dp))

        // 2. Details Column
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            var isTruncated by remember { mutableStateOf(false) }

            Text(
                text = name,
                style = MaterialTheme.typography.headlineLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(4.dp))

            bio?.let {
                Column {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = if (isBioExpanded) Int.MAX_VALUE else 3,
                        onTextLayout = { textLayoutResult ->
                            if (!isBioExpanded) {
                                isTruncated = textLayoutResult.hasVisualOverflow
                            }
                        }
                    )
                    if (isTruncated || isBioExpanded) {
                        Text(
                            text = if (isBioExpanded) stringResource(R.string.show_less) else stringResource(R.string.read_more),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            ),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .clickable { isBioExpanded = !isBioExpanded }
                                .padding(vertical = 4.dp)
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
            }

            location?.let {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Place,
                        contentDescription = stringResource(R.string.location),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(Modifier.height(4.dp))
            }

            portfolioUrl?.let { url ->
                val uriHandler = LocalUriHandler.current
                val displayUrl =
                    try {
                        URI(url).host?.removePrefix("www.") ?: url
                    } catch (e: Exception) {
                        url
                    }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Link,
                        contentDescription = stringResource(R.string.portfolio),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = displayUrl,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.clickable { uriHandler.openUri(url) }
                    )
                }
                Spacer(Modifier.height(4.dp))
            }

            instagramUsername?.let {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = stringResource(R.string.instagram),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "@$it",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(Modifier.height(4.dp))
            }

            // Unsplash Profile Link
            val uriHandler = LocalUriHandler.current
            val appName = stringResource(R.string.app_name)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { 
                    val urlWithUtm = if (unsplashProfileUrl.contains("?")) {
                        "$unsplashProfileUrl&utm_source=$appName&utm_medium=referral"
                    } else {
                        "$unsplashProfileUrl?utm_source=$appName&utm_medium=referral"
                    }
                    uriHandler.openUri(urlWithUtm) 
                }
            ) {
                Icon(
                    Icons.Default.Link,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = stringResource(R.string.view_on_unsplash),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(Modifier.height(8.dp))

            ConfettiButton(onFollowChanged = {})
        }
    }
}
