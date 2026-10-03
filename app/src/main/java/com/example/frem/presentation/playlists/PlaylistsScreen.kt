package com.example.frem.presentation.playlists

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.frem.domain.model.Playlist
@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun PlaylistsScreen(
    viewModel: PlaylistsViewModel,
    onBack: () -> Unit,
    onPlaylistClick: (Playlist) -> Unit
) {
    val playlists by viewModel.playlists.collectAsState()

    var showCreate by remember {
        mutableStateOf(false)
    }

    var name by remember {
        mutableStateOf("")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Playlists")
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            name = ""
                            showCreate = true
                        }
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Create playlist"
                        )
                    }
                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            items(
                playlists,
                key = { it.id }
            ) { playlist ->

                ListItem(
                    modifier = Modifier.fillMaxWidth(),
                    leadingContent = {
                        Icon(
                            Icons.Default.PlaylistPlay,
                            contentDescription = null
                        )
                    },
                    headlineContent = {
                        Text(playlist.name)
                    },
                    trailingContent = {
                        IconButton(
                            onClick = {
                                viewModel.deletePlaylist(
                                    playlist.id
                                )
                            }
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete"
                            )
                        }
                    }
                )
            }
        }
    }

    if (showCreate) {
        AlertDialog(
            onDismissRequest = {
                showCreate = false
            },
            title = {
                Text("New playlist")
            },
            text = {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                    },
                    label = {
                        Text("Playlist name")
                    },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    enabled = name.isNotBlank(),
                    onClick = {
                        viewModel.createPlaylist(
                            name.trim()
                        )
                        showCreate = false
                    }
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showCreate = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}