package com.example.frem.presentation.songs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.frem.domain.model.Song
import com.example.frem.ui.components.SongRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongsScreen(
    viewModel: SongsViewModel,
    onBack: () -> Unit,
    onSongClick: (Song) -> Unit
) {
    val songs by viewModel.songs.collectAsState()
    val query by viewModel.searchQuery.collectAsState()

    var showSearch by remember {
        mutableStateOf(false)
    }

    var showSort by remember {
        mutableStateOf(false)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (showSearch) {
                        OutlinedTextField(
                            value = query,
                            onValueChange = viewModel::setSearchQuery,
                            placeholder = {
                                Text("Search songs")
                            },
                            singleLine = true
                        )
                    } else {
                        Text("Songs")
                    }
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
                            showSearch = !showSearch
                            if (!showSearch) {
                                viewModel.setSearchQuery("")
                            }
                        }
                    ) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    }

                    IconButton(
                        onClick = {
                            showSort = true
                        }
                    ) {
                        Icon(
                            Icons.Default.Sort,
                            contentDescription = "Sort"
                        )
                    }

                    DropdownMenu(
                        expanded = showSort,
                        onDismissRequest = {
                            showSort = false
                        }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Title") },
                            onClick = {
                                viewModel.setSort(SongSort.TITLE)
                                showSort = false
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Artist") },
                            onClick = {
                                viewModel.setSort(SongSort.ARTIST)
                                showSort = false
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Album") },
                            onClick = {
                                viewModel.setSort(SongSort.ALBUM)
                                showSort = false
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Recently played") },
                            onClick = {
                                viewModel.setSort(SongSort.RECENTLY_PLAYED)
                                showSort = false
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Most played") },
                            onClick = {
                                viewModel.setSort(SongSort.MOST_PLAYED)
                                showSort = false
                            }
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${songs.size} songs"
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (songs.isEmpty()) {
                Text(
                    text = if (query.isBlank()) {
                        "No music found"
                    } else {
                        "No songs found"
                    }
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    items(
                        items = songs,
                        key = { it.id }
                    ) { song ->

                        SongRow(
                            song = song,
                            onClick = {
                                onSongClick(song)
                            },
                            onFavoriteClick = {
                                viewModel.toggleFavorite(song)
                            }
                        )
                    }
                }
            }
        }
    }
}