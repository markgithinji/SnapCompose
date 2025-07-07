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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
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
    modifier: Modifier = Modifier
) {
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        val (profileImageRef, columnContent) = createRefs()

        UserProfileImage(
            imageUrl = profileImage,
            stringResource(R.string.profile_picture_desc, name),
            modifier = Modifier
                .size(140.dp)
                .constrainAs(profileImageRef) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
        )

        Column(
            modifier = Modifier
                .constrainAs(columnContent) {
                    start.linkTo(profileImageRef.end, margin = 36.dp)
                    top.linkTo(profileImageRef.top)
                    bottom.linkTo(profileImageRef.bottom)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(name, style = MaterialTheme.typography.headlineLarge)
            Spacer(Modifier.height(4.dp))

            bio?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 8
                )
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
                    Text(it, style = MaterialTheme.typography.labelMedium)
                }
                Spacer(Modifier.height(4.dp))
            }

            portfolioUrl?.let { url ->
                val uriHandler = LocalUriHandler.current
                val displayUrl =
                    try { // A shorter alternative urls. Showing full URLs can be messy.
                        URI(url).host.removePrefix("www.")
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
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                Spacer(Modifier.height(8.dp))
            }

            ConfettiButton(onFollowChanged = {})
        }
    }
}

