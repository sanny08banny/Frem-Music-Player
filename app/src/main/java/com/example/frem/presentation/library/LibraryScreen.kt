
package com.example.frem.presentation.library

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val LibraryBackground = Color(0xFF0B0D12)
private val LibraryCard = Color(0xFF151922)
private val LibraryBorder = Color(0xFF292F3B)
private val LibraryPurple = Color(0xFFB9A4FF)
private val LibrarySecondary = Color(0xFF9299A8)

@Composable
fun LibraryScreen(
    onSongsClick: () -> Unit,
    onFoldersClick: () -> Unit,
    onPlaylistsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LibraryBackground)
            .padding(horizontal = 20.dp)
            .padding(top = 28.dp)
    ) {
        Text(
            text = "YOUR COLLECTION",
            color = LibraryPurple,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )

        Text(
            text = "Library",
            color = Color.White,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 8.dp)
        )

        Text(
            text = "Browse and manage your music",
            color = LibrarySecondary,
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 6.dp, bottom = 28.dp)
        )

        LibraryItem(
            title = "Songs",
            description = "Listen to all your music",
            icon = Icons.Default.MusicNote,
            onClick = onSongsClick
        )

        LibraryItem(
            title = "Folders",
            description = "Browse music by folder",
            icon = Icons.Default.Folder,
            onClick = onFoldersClick
        )

        LibraryItem(
            title = "Playlists",
            description = "Your personal collections",
            icon = Icons.Default.PlaylistPlay,
            onClick = onPlaylistsClick
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "YOUR MUSIC. YOUR SPACE.",
            color = Color(0xFF555D6C),
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 1.5.sp,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 24.dp)
        )
    }
}

@Composable
private fun LibraryItem(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = LibraryCard,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            LibraryBorder
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(
                        color = LibraryPurple.copy(alpha = 0.13f),
                        shape = RoundedCornerShape(15.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = LibraryPurple,
                    modifier = Modifier.size(25.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = description,
                    color = LibrarySecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Open $title",
                tint = LibrarySecondary,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
