
package com.example.frem.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.statusBarsPadding
import com.example.frem.data.media.FremPlayer
import com.example.frem.domain.model.Song
import com.example.frem.ui.components.SongRow

private val HomeBackground = Color(0xFF0D0F12)
private val HomeSurface = Color(0xFF181B21)
private val HomeSurfaceVariant = Color(0xFF22262E)
private val HomeAccent = Color(0xFFB7F36B)
private val HomeText = Color(0xFFF4F5F7)
private val HomeMuted = Color(0xFF9298A3)
private val HomeDivider = Color(0xFF292D35)

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    fremPlayer: FremPlayer,
    onSongsClick: () -> Unit,
    onAlbumsClick: () -> Unit,
    onArtistsClick: () -> Unit,
    onFoldersClick: () -> Unit,
    onPlaylistsClick: () -> Unit,
    onSongClick: (Song) -> Unit,
    onRecentlyPlayedClick: () -> Unit,
    onQueueClick: () -> Unit
) {
    val recentlyPlayed by viewModel.recentlyPlayed.collectAsState()
    val queue by fremPlayer.queue.collectAsState()
    val currentSong by fremPlayer.currentSong.collectAsState()
    val isPlaying by fremPlayer.isPlaying.collectAsState()
    val activeQueue by fremPlayer.activeQueue.collectAsState()

    val quickAccessItems = listOf(
        QuickAccessData(
            "Songs",
            "Your music library",
            Icons.Default.MusicNote,
            onSongsClick
        ),
        QuickAccessData(
            "Albums",
            "Browse albums",
            Icons.Default.Album,
            onAlbumsClick
        ),
        QuickAccessData(
            "Artists",
            "Find your artists",
            Icons.Default.Person,
            onArtistsClick
        ),
        QuickAccessData(
            "Folders",
            "Browse folders",
            Icons.Default.Folder,
            onFoldersClick
        ),
        QuickAccessData(
            "Playlists",
            "Your collections",
            Icons.Default.PlaylistPlay,
            onPlaylistsClick
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HomeBackground)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {

        Spacer(Modifier.height(24.dp))

        // Recently played
        RecentlyPlayedSection(
            songs = recentlyPlayed,
            onViewAll = onRecentlyPlayedClick,
            onSongClick = onSongClick
        )

        Spacer(Modifier.height(34.dp))

        // Library
        Column {
            Text(
                text = "LIBRARY",
                style = MaterialTheme.typography.labelSmall,
                color = HomeAccent,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(7.dp))

            Text(
                text = "Your music",
                style = MaterialTheme.typography.headlineSmall,
                color = HomeText,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(5.dp))

            Text(
                text = "Everything you love, one tap away.",
                style = MaterialTheme.typography.bodySmall,
                color = HomeMuted
            )
        }

        Spacer(Modifier.height(18.dp))

        // Quick access grid
        quickAccessItems.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEach { item ->
                    QuickAccessTile(
                        item = item,
                        modifier = Modifier.weight(1f)
                    )
                }

                if (rowItems.size == 1) {
                    Spacer(Modifier.weight(1f))
                }
            }

            Spacer(Modifier.height(10.dp))
        }

        Spacer(Modifier.height(30.dp))

        // Current queue
        CurrentQueueSection(
            songs = queue,
            currentSong = currentSong,
            isPlaying = isPlaying,
            queueName = activeQueue?.name ?: "Queue",
            onViewQueue = onQueueClick,
            onSongClick = { song ->
                fremPlayer.playFromQueue(song)
            }
        )

        Spacer(Modifier.height(30.dp))
    }
}

// ----------------------------------------------------
// RECENTLY PLAYED
// ----------------------------------------------------

@Composable
private fun RecentlyPlayedSection(
    songs: List<Song>,
    onViewAll: () -> Unit,
    onSongClick: (Song) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onViewAll),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "RECENTLY PLAYED",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.labelSmall,
                color = HomeMuted,
                letterSpacing = 1.7.sp,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "VIEW ALL",
                style = MaterialTheme.typography.labelSmall,
                color = HomeAccent,
                letterSpacing = 0.8.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.width(5.dp))

            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "View recently played",
                tint = HomeAccent,
                modifier = Modifier.size(15.dp)
            )
        }

        Spacer(Modifier.height(15.dp))

        if (songs.isEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(HomeSurface)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    tint = HomeMuted,
                    modifier = Modifier.size(23.dp)
                )

                Spacer(Modifier.width(13.dp))

                Column {
                    Text(
                        text = "Nothing played yet",
                        color = HomeText,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(Modifier.height(3.dp))

                    Text(
                        text = "Your listening history will appear here.",
                        color = HomeMuted,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        } else {
            val lastSong = songs.first()

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    SongRow(
                        song = lastSong,
                        compact = true,
                        onClick = { onSongClick(lastSong) }
                    )
                }

                Spacer(Modifier.width(12.dp))

                IconButton(
                    onClick = { onSongClick(lastSong) },
                    modifier = Modifier
                        .size(42.dp)
                        .background(
                            HomeAccent,
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play recently played song",
                        tint = HomeBackground,
                        modifier = Modifier.size(25.dp)
                    )
                }
            }
        }
    }
}

// ----------------------------------------------------
// CURRENT QUEUE
// ----------------------------------------------------

@Composable
private fun CurrentQueueSection(
    songs: List<Song>,
    currentSong: Song?,
    isPlaying: Boolean,
    queueName: String,
    onViewQueue: () -> Unit,
    onSongClick: (Song) -> Unit
) {
    val currentIndex = songs.indexOfFirst {
        it.id == currentSong?.id
    }

    val visibleSongs = if (currentIndex >= 0) {
        songs.drop(currentIndex).take(4)
    } else {
        songs.take(4)
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        // Editorial-style header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onViewQueue),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "UP NEXT",
                    style = MaterialTheme.typography.labelSmall,
                    color = HomeMuted,
                    letterSpacing = 1.8.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = queueName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = HomeText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = songs.size.toString().padStart(2, '0'),
                    style = MaterialTheme.typography.titleMedium,
                    color = HomeText,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "TRACKS",
                    style = MaterialTheme.typography.labelSmall,
                    color = HomeMuted,
                    letterSpacing = 1.sp
                )
            }

            Spacer(Modifier.width(12.dp))

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Open full queue",
                tint = HomeMuted,
                modifier = Modifier.size(21.dp)
            )
        }

        Spacer(Modifier.height(17.dp))

        HorizontalDivider(
            color = HomeDivider,
            thickness = 0.8.dp
        )

        if (songs.isEmpty()) {

            // Minimal empty state
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onViewQueue)
                    .padding(vertical = 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.QueueMusic,
                    contentDescription = null,
                    tint = HomeMuted.copy(alpha = 0.65f),
                    modifier = Modifier.size(26.dp)
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Your queue is empty",
                    color = HomeText,
                    fontWeight = FontWeight.Medium,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "Play something to get started.",
                    color = HomeMuted,
                    style = MaterialTheme.typography.bodySmall
                )
            }

        } else {

            // Track list
            visibleSongs.forEachIndexed { index, song ->
                val isCurrent = song.id == currentSong?.id

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onSongClick(song)
                        }
                        .padding(vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // Track number / playing indicator
                    Box(
                        modifier = Modifier.width(25.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (isCurrent) {
                            Box(
                                modifier = Modifier
                                    .width(3.dp)
                                    .height(27.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(HomeAccent)
                            )
                        } else {
                            Text(
                                text = "%02d".format(
                                    (currentIndex + index + 1).coerceAtLeast(1)
                                ),
                                color = HomeMuted.copy(alpha = 0.75f),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }

                    Spacer(Modifier.width(5.dp))

                    Box(
                        modifier = Modifier.weight(1f)
                    ) {
                        SongRow(
                            song = song,
                            compact = true,
                            onClick = {
                                onSongClick(song)
                            }
                        )
                    }

                    if (isCurrent) {
                        Spacer(Modifier.width(8.dp))

                        Icon(
                            imageVector = if (isPlaying) {
                                Icons.Default.GraphicEq
                            } else {
                                Icons.Default.Pause
                            },
                            contentDescription = if (isPlaying) {
                                "Now playing"
                            } else {
                                "Playback paused"
                            },
                            tint = HomeAccent,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                }

                if (index < visibleSongs.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 30.dp),
                        color = HomeDivider.copy(alpha = 0.75f),
                        thickness = 0.6.dp
                    )
                }
            }

            // Queue footer
            if (songs.size > visibleSongs.size) {
                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = onViewQueue)
                        .padding(vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${songs.size - visibleSongs.size} more tracks",
                        color = HomeMuted,
                        style = MaterialTheme.typography.bodySmall
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "View all",
                            color = HomeAccent,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(Modifier.width(5.dp))

                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = HomeAccent,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// QUICK ACCESS
// ----------------------------------------------------

private data class QuickAccessData(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@Composable
private fun QuickAccessTile(
    item: QuickAccessData,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(76.dp)
            .clickable(onClick = item.onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = HomeSurface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = HomeAccent.copy(alpha = 0.85f),
                modifier = Modifier.size(21.dp)
            )

            Spacer(Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = item.title,
                    color = HomeText,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(3.dp))

                Text(
                    text = item.subtitle,
                    color = HomeMuted,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Open ${item.title}",
                tint = HomeMuted.copy(alpha = 0.65f),
                modifier = Modifier.size(17.dp)
            )
        }
    }
}
