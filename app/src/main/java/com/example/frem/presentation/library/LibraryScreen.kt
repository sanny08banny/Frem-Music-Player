package com.example.frem.presentation.library

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun LibraryScreen(
    onSongsClick: () -> Unit,
    onFoldersClick: () -> Unit,
    onPlaylistsClick: () -> Unit
) {

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        Text(
            text = "Library"
        )

        ListItem(
            headlineContent = {
                Text("Songs")
            },
            supportingContent = {
                Text("All songs")
            },
            leadingContent = {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null
                )
            },
            modifier = Modifier.clickable {
                onSongsClick()
            }
        )

        ListItem(
            headlineContent = {
                Text("Folders")
            },
            supportingContent = {
                Text("Browse music folders")
            },
            leadingContent = {
                Icon(
                    imageVector = Icons.Default.Folder,
                    contentDescription = null
                )
            },
            modifier = Modifier.clickable {
                onFoldersClick()
            }
        )

        ListItem(
            headlineContent = {
                Text("Playlists")
            },
            supportingContent = {
                Text("Your playlists")
            },
            leadingContent = {
                Icon(
                    imageVector = Icons.Default.PlaylistPlay,
                    contentDescription = null
                )
            },
            modifier = Modifier.clickable {
                onPlaylistsClick()
            }
        )
    }
}