package com.example.frem.presentation.albums


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.frem.domain.model.Album
@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun AlbumsScreen(
    viewModel: AlbumsViewModel,
    onBack: () -> Unit,
    onAlbumClick: (Album) -> Unit
) {
    val albums by viewModel.albums.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Albums")
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->

        LazyVerticalGrid(
            columns = GridCells.Adaptive(150.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            items(
                albums,
                key = { it.id }
            ) { album ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {

                    Column(
                        modifier = Modifier
                            .padding(bottom = 10.dp)
                    ) {

                        if (album.artworkUri != null) {
                            AsyncImage(
                                model = album.artworkUri,
                                contentDescription = album.title,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onAlbumClick(album)
                                    },
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(48.dp)
                            )
                        }

                        Text(
                            text = album.title,
                            modifier = Modifier.padding(
                                horizontal = 10.dp
                            ),
                            maxLines = 1,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = album.artist,
                            modifier = Modifier.padding(
                                horizontal = 10.dp
                            ),
                            maxLines = 1,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}