
package com.example.frem.presentation.albums

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.frem.domain.model.Album

private val AlbumsBackground = Color(0xFF0B0D12)
private val AlbumCardBackground = Color(0xFF151922)
private val AlbumBorder = Color(0xFF292F3B)
private val AlbumAccent = Color(0xFFB9A4FF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumsScreen(
    viewModel: AlbumsViewModel,
    onBack: () -> Unit,
    onAlbumClick: (Album) -> Unit
) {
    val albums by viewModel.albums.collectAsState()

    Scaffold(
        containerColor = AlbumsBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Your collection",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "${albums.size} albums",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF9299A8)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AlbumsBackground
                )
            )
        }
    ) { innerPadding ->

        when {
            albums.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .background(AlbumsBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Album,
                            contentDescription = null,
                            tint = AlbumAccent,
                            modifier = Modifier.padding(8.dp)
                        )
                        Text(
                            text = "No albums found",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        Text(
                            text = "Your music collection will appear here.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF9299A8)
                        )
                    }
                }
            }

            else -> {
                LazyVerticalGrid(
                    // Always exactly three columns.
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(
                        start = 12.dp,
                        end = 12.dp,
                        top = 8.dp,
                        bottom = 24.dp
                    ),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = albums,
                        key = { it.id }
                    ) { album ->

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onAlbumClick(album)
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = AlbumCardBackground
                            ),
                            border = CardDefaults.outlinedCardBorder(
                                enabled = true
                            ).copy(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        AlbumBorder,
                                        Color(0xFF202633)
                                    )
                                )
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 0.dp,
                                pressedElevation = 2.dp
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(6.dp)
                            ) {
                                // Compact, square album artwork.
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1f)
                                        .clip(
                                            RoundedCornerShape(8.dp)
                                        )
                                        .background(
                                            Brush.linearGradient(
                                                colors = listOf(
                                                    Color(0xFF292541),
                                                    Color(0xFF171B27)
                                                )
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (album.artworkUri != null) {
                                        AsyncImage(
                                            model = album.artworkUri,
                                            contentDescription =
                                                "${album.title} artwork",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Icon(
                                            imageVector =
                                                Icons.Default.MusicNote,
                                            contentDescription = null,
                                            tint = AlbumAccent,
                                            modifier = Modifier
                                                .fillMaxSize(0.42f)
                                        )
                                    }
                                }

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            start = 2.dp,
                                            end = 2.dp,
                                            top = 8.dp,
                                            bottom = 4.dp
                                        ),
                                    verticalArrangement =
                                        Arrangement.spacedBy(3.dp)
                                ) {
                                    Text(
                                        text = album.title,
                                        color = Color.White,
                                        style = MaterialTheme.typography
                                            .labelLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Text(
                                        text = album.artist.ifBlank {
                                            "Unknown artist"
                                        },
                                        color = Color(0xFF9299A8),
                                        style = MaterialTheme.typography
                                            .labelSmall,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
