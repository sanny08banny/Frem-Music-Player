
package com.example.frem.presentation.albums

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.frem.domain.model.Album

private val AlbumsBackground = Color(0xFF0B0D12)
private val AlbumCardBackground = Color(0xFF151922)
private val AlbumBorder = Color(0xFF292F3B)
private val AlbumAccent = Color(0xFFB9A4FF)
private val AlbumSecondary = Color(0xFF9299A8)
private val AlbumArtworkStart = Color(0xFF302A4C)
private val AlbumArtworkEnd = Color(0xFF171B27)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumsScreen(
    viewModel: AlbumsViewModel,
    onBack: () -> Unit,
    onAlbumClick: (Album) -> Unit
) {
    val albums by viewModel.albums.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val refreshError by viewModel.refreshError.collectAsState()

    Scaffold(
        containerColor = AlbumsBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column(
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Your Library",
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "${albums.size} albums",
                            color = AlbumSecondary,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.refreshAlbums()
                        },
                        enabled = !isRefreshing
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh albums",
                            tint = if (isRefreshing) {
                                AlbumSecondary
                            } else {
                                AlbumAccent
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AlbumsBackground,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                viewModel.refreshAlbums()
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            indicator = {
                PullToRefreshDefaults.Indicator(
                    state = rememberPullToRefreshState(),
                    isRefreshing = isRefreshing,
                    containerColor = AlbumCardBackground,
                    color = AlbumAccent
                )
            }
        ) {

            if (albums.isEmpty()) {

                // Empty library state.
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(AlbumsBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(32.dp)
                    ) {

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(24.dp))
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFF302A4C),
                                            Color(0xFF191D2A)
                                        )
                                    )
                                )
                                .padding(28.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Album,
                                contentDescription = null,
                                tint = AlbumAccent,
                                modifier = Modifier.height(48.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "Your library is empty",
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Your albums will appear here once your music is discovered.",
                            color = AlbumSecondary,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Pull down to refresh",
                            color = AlbumAccent,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        if (refreshError != null) {
                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = refreshError ?: "",
                                color = Color(0xFFFF8A8A),
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

            } else {

                Column(
                    modifier = Modifier.fillMaxSize()
                ) {

                    // Collection heading.
                    Column(
                        modifier = Modifier.padding(
                            start = 16.dp,
                            end = 16.dp,
                            top = 12.dp,
                            bottom = 14.dp
                        )
                    ) {
                        Text(
                            text = "Albums",
                            color = Color.White,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Explore your music collection",
                            color = AlbumSecondary,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    // Album grid.
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 12.dp,
                            end = 12.dp,
                            top = 4.dp,
                            bottom = 32.dp
                        ),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        items(
                            items = albums,
                            key = { it.id }
                        ) { album ->

                            Card(
                                onClick = {
                                    onAlbumClick(album)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .animateContentSize(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = AlbumCardBackground
                                ),
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = AlbumBorder
                                ),
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 0.dp,
                                    pressedElevation = 3.dp
                                )
                            ) {

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(6.dp)
                                ) {

                                    // Artwork.
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(1f)
                                            .clip(
                                                RoundedCornerShape(10.dp)
                                            )
                                            .background(
                                                Brush.linearGradient(
                                                    colors = listOf(
                                                        AlbumArtworkStart,
                                                        AlbumArtworkEnd
                                                    )
                                                )
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {

                                        if (album.artworkUri != null) {

                                            AsyncImage(
                                                model = album.artworkUri,
                                                contentDescription = "${album.title} artwork",
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )

                                        } else {

                                            Icon(
                                                imageVector = Icons.Default.MusicNote,
                                                contentDescription = null,
                                                tint = AlbumAccent,
                                                modifier = Modifier.fillMaxSize(
                                                    0.38f
                                                )
                                            )
                                        }
                                    }

                                    // Album details.
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(
                                                start = 3.dp,
                                                end = 3.dp,
                                                top = 9.dp,
                                                bottom = 5.dp
                                            ),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {

                                        Text(
                                            text = album.title.ifBlank {
                                                "Unknown album"
                                            },
                                            color = Color.White,
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.SemiBold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Text(
                                            text = album.artist.ifBlank {
                                                "Unknown artist"
                                            },
                                            color = AlbumSecondary,
                                            style = MaterialTheme.typography.labelSmall,
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

            // Show refresh errors even when albums already exist.
            if (refreshError != null && albums.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Text(
                        text = refreshError ?: "",
                        color = Color(0xFFFF8A8A),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(AlbumCardBackground)
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }
            }
        }
    }
}
