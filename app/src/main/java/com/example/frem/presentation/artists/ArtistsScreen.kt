
package com.example.frem.presentation.artists

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.frem.domain.model.Artist

private val ArtistsBackground = Color(0xFF0B0D12)
private val ArtistCardBackground = Color(0xFF151922)
private val ArtistBorder = Color(0xFF292F3B)
private val ArtistAccent = Color(0xFFB9A4FF)
private val ArtistSecondary = Color(0xFF9299A8)

private val ArtistAvatarGradients = listOf(
    Color(0xFF302A4C),
    Color(0xFF26334A),
    Color(0xFF49304C),
    Color(0xFF253F42),
    Color(0xFF45352C)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistsScreen(
    viewModel: ArtistsViewModel,
    onBack: () -> Unit,
    onArtistClick: (Artist) -> Unit
) {
    val artists by viewModel.artists.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val refreshError by viewModel.refreshError.collectAsState()

    Scaffold(
        containerColor = ArtistsBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Your Library",
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "${artists.size} artists",
                            color = ArtistSecondary,
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
                            viewModel.refreshArtists()
                        },
                        enabled = !isRefreshing
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh artists",
                            tint = if (isRefreshing) {
                                ArtistSecondary
                            } else {
                                ArtistAccent
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ArtistsBackground,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                viewModel.refreshArtists()
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            indicator = {
                PullToRefreshDefaults.Indicator(
                    state = rememberPullToRefreshState(),
                    isRefreshing = isRefreshing,
                    containerColor = ArtistCardBackground,
                    color = ArtistAccent
                )
            }
        ) {

            if (artists.isEmpty()) {

                // Empty artists state.
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(ArtistsBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(32.dp)
                    ) {

                        Box(
                            modifier = Modifier
                                .size(104.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFF302A4C),
                                            Color(0xFF191D2A)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = ArtistAccent,
                                modifier = Modifier.size(46.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "No artists found",
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Your artists will appear here once your music is discovered.",
                            color = ArtistSecondary,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Pull down to refresh",
                            color = ArtistAccent,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        if (refreshError != null) {
                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = refreshError ?: "",
                                color = Color(0xFFFF8A8A),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

            } else {

                Column(
                    modifier = Modifier.fillMaxSize()
                ) {

                    // Section heading.
                    Column(
                        modifier = Modifier.padding(
                            start = 16.dp,
                            end = 16.dp,
                            top = 12.dp,
                            bottom = 14.dp
                        )
                    ) {
                        Text(
                            text = "Artists",
                            color = Color.White,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Discover the artists behind your music",
                            color = ArtistSecondary,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    // Artist list.
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            horizontal = 12.dp,
                            vertical = 4.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        items(
                            items = artists,
                            key = { it.id }
                        ) { artist ->

                            Card(
                                onClick = {
                                    onArtistClick(artist)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = ArtistCardBackground
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    width = 1.dp,
                                    color = ArtistBorder
                                ),
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 0.dp,
                                    pressedElevation = 2.dp
                                )
                            ) {

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                                ) {

                                    // Artist avatar.
                                    val avatarColor =
                                        ArtistAvatarGradients[
                                            (artist.id.hashCode().toLong()
                                                .and(Long.MAX_VALUE) %
                                                    ArtistAvatarGradients.size).toInt()
                                        ]

                                    Box(
                                        modifier = Modifier
                                            .size(58.dp)
                                            .clip(CircleShape)
                                            .background(
                                                Brush.linearGradient(
                                                    colors = listOf(
                                                        avatarColor,
                                                        Color(0xFF191D2A)
                                                    )
                                                )
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {

                                        val initials = artist.name
                                            .trim()
                                            .split(Regex("\\s+"))
                                            .filter { it.isNotBlank() }
                                            .take(2)
                                            .joinToString("") {
                                                it.first().uppercase()
                                            }

                                        if (initials.isNotEmpty()) {
                                            Text(
                                                text = initials,
                                                color = Color.White,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                        } else {
                                            Icon(
                                                imageVector = Icons.Default.Person,
                                                contentDescription = null,
                                                tint = ArtistAccent,
                                                modifier = Modifier.size(28.dp)
                                            )
                                        }
                                    }

                                    // Artist information.
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        verticalArrangement = Arrangement.spacedBy(5.dp)
                                    ) {

                                        Text(
                                            text = artist.name.ifBlank {
                                                "Unknown artist"
                                            },
                                            color = Color.White,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.SemiBold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {

                                            Text(
                                                text = "${artist.albumCount} albums",
                                                color = ArtistSecondary,
                                                style = MaterialTheme.typography.labelSmall
                                            )

                                            Text(
                                                text = "•",
                                                color = ArtistAccent,
                                                style = MaterialTheme.typography.labelSmall
                                            )

                                            Text(
                                                text = "${artist.songCount} songs",
                                                color = ArtistSecondary,
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                        }
                                    }

                                    // Music icon.
                                    Icon(
                                        imageVector = Icons.Default.MusicNote,
                                        contentDescription = null,
                                        tint = ArtistAccent.copy(alpha = 0.75f),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Refresh error when the list already contains artists.
            if (refreshError != null && artists.isNotEmpty()) {
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
                            .background(ArtistCardBackground)
                            .padding(
                                horizontal = 16.dp,
                                vertical = 12.dp
                            )
                    )
                }
            }
        }
    }
}
