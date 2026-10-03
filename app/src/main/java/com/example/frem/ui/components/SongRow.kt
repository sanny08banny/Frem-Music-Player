package com.example.frem.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.frem.domain.model.Song

@Composable
fun SongRow(
    song: Song,
    onClick: () -> Unit,
    onFavoriteClick: (() -> Unit)? = null,
    compact: Boolean = false
) {
    val artworkSize = if (compact) 64.dp else 56.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .height(if (compact) 80.dp else 72.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(artworkSize)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (song.albumArtworkUri != null) {
                AsyncImage(
                    model = song.albumArtworkUri,
                    contentDescription = song.album,
                    modifier = Modifier.size(artworkSize),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = song.title,
                maxLines = 1,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = "${song.artist} • ${song.album}",
                maxLines = 1,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )
        }

        if (onFavoriteClick != null) {
            IconButton(
                onClick = onFavoriteClick
            ) {
                Text(
                    text = if (song.isFavorite) "♥" else "♡"
                )
            }
        }
    }
}