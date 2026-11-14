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
import com.example.frem.data.media.FremPlayer
import com.example.frem.domain.model.Song
import com.example.frem.ui.components.SongRow

private val HomeBackground = Color(0xFF0D0F12)
private val HomeSurface = Color(0xFF181B21)
private val HomeSurfaceVariant = Color(0xFF22262E)
private val HomeAccent = Color(0xFFB7F36B)
private val HomeText = Color(0xFFF4F5F7)
private val HomeMuted = Color(0xFF9298A3)

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
        QuickAccessData("Songs", "Your music library", Icons.Default.MusicNote, onSongsClick),
        QuickAccessData("Albums", "Browse albums", Icons.Default.Album, onAlbumsClick),
        QuickAccessData("Artists", "Find your artists", Icons.Default.Person, onArtistsClick),
        QuickAccessData("Folders", "Browse folders", Icons.Default.Folder, onFoldersClick),
        QuickAccessData("Playlists", "Your collections", Icons.Default.PlaylistPlay, onPlaylistsClick)
    )

    Column(
        modifier = Modifier.fillMaxSize()
            .background(HomeBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {

        Spacer(Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = HomeSurface)
        ) {
            Column(Modifier.fillMaxWidth().padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().clickable(onClick = onRecentlyPlayedClick),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.History,
                        contentDescription = null,
                        tint = HomeMuted,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Recently Played",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.labelMedium,
                        color = HomeMuted
                    )
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = "View recently played",
                        tint = HomeMuted,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(Modifier.height(14.dp))

                if (recentlyPlayed.isEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            color = HomeSurfaceVariant
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.MusicNote,
                                    contentDescription = null,
                                    tint = HomeAccent,
                                    modifier = Modifier.size(25.dp)
                                )
                            }
                        }
                        Spacer(Modifier.width(14.dp))
                        Column {
                            Text(
                                "Nothing played yet",
                                color = HomeText,
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.titleSmall
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "Your listening history will show up here.",
                                color = HomeMuted,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                } else {
                    val lastSong = recentlyPlayed.first()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(Modifier.weight(1f)) {
                            SongRow(
                                song = lastSong,
                                compact = true,
                                onClick = { onSongClick(lastSong) }
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Surface(
                            modifier = Modifier.size(44.dp),
                            shape = CircleShape,
                            color = HomeAccent
                        ) {
                            IconButton(onClick = { onSongClick(lastSong) }) {
                                Icon(
                                    Icons.Default.PlayArrow,
                                    contentDescription = "Play recently played song",
                                    tint = HomeBackground,
                                    modifier = Modifier.size(29.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        Column {
            Text(
                "YOUR LIBRARY",
                style = MaterialTheme.typography.labelMedium,
                color = HomeAccent,
                letterSpacing = 1.8.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(5.dp))
            Text(
                "Quick Access",
                style = MaterialTheme.typography.titleLarge,
                color = HomeText,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(5.dp))
            Text(
                "Everything you love, one tap away.",
                style = MaterialTheme.typography.bodyMedium,
                color = HomeMuted
            )
        }

        Spacer(Modifier.height(16.dp))

        quickAccessItems.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEach { item ->
                    QuickAccessTile(item = item, modifier = Modifier.weight(1f))
                }
                if (rowItems.size == 1) Spacer(Modifier.weight(1f))
            }
            Spacer(Modifier.height(12.dp))
        }

        Spacer(Modifier.height(12.dp))

        CurrentQueueSection(
            songs = queue,
            currentSong = currentSong,
            isPlaying = isPlaying,
            queueName = activeQueue?.name ?: "Queue",
            onViewQueue = onQueueClick,
            onSongClick = { song -> fremPlayer.playFromQueue(song) }
        )

        Spacer(Modifier.height(28.dp))
    }
}

@Composable
private fun CurrentQueueSection(
    songs: List<Song>,
    currentSong: Song?,
    isPlaying: Boolean,
    queueName: String,
    onViewQueue: () -> Unit,
    onSongClick: (Song) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = HomeSurface)
    ) {
        Column(Modifier.fillMaxWidth().padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = onViewQueue)
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(42.dp),
                    shape = RoundedCornerShape(13.dp),
                    color = HomeSurfaceVariant
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.QueueMusic,
                            contentDescription = null,
                            tint = HomeAccent,
                            modifier = Modifier.size(23.dp)
                        )
                    }
                }

                Spacer(Modifier.width(12.dp))

                Column(Modifier.weight(1f)) {
                    Text(
                        "Current Queue",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = HomeText
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "$queueName · ${songs.size} songs",
                        style = MaterialTheme.typography.bodySmall,
                        color = HomeMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = "Open full queue",
                    tint = HomeMuted,
                    modifier = Modifier.size(25.dp)
                )
            }

            Spacer(Modifier.height(12.dp))

            if (songs.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(15.dp))
                        .background(Color(0xFF121417))
                        .clickable(onClick = onViewQueue)
                        .padding(horizontal = 14.dp, vertical = 20.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.PlaylistPlay,
                            contentDescription = null,
                            tint = HomeMuted,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                "Your queue is empty",
                                color = HomeText,
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(Modifier.height(3.dp))
                            Text(
                                "Play a song to start your queue.",
                                color = HomeMuted,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            } else {
                val currentIndex = songs.indexOfFirst { it.id == currentSong?.id }
                val visibleSongs = if (currentIndex >= 0) {
                    songs.drop(currentIndex).take(4)
                } else {
                    songs.take(4)
                }

                visibleSongs.forEachIndexed { index, song ->
                    val isCurrent = song.id == currentSong?.id

                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(13.dp))
                            .background(if (isCurrent) Color(0xFF1C3024) else Color.Transparent)
                            .clickable { onSongClick(song) }
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(Modifier.width(27.dp), contentAlignment = Alignment.Center) {
                            if (isCurrent && isPlaying) {
                                Icon(
                                    Icons.Default.GraphicEq,
                                    contentDescription = "Now playing",
                                    tint = HomeAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                            } else if (isCurrent) {
                                Icon(
                                    Icons.Default.Pause,
                                    contentDescription = "Current song paused",
                                    tint = HomeAccent,
                                    modifier = Modifier.size(19.dp)
                                )
                            } else {
                                Text(
                                    text = (currentIndex + index + 1).coerceAtLeast(1).toString(),
                                    color = HomeMuted,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }

                        Spacer(Modifier.width(8.dp))

                        Box(Modifier.weight(1f)) {
                            SongRow(
                                song = song,
                                compact = true,
                                onClick = { onSongClick(song) }
                            )
                        }

                        if (isCurrent) {
                            Spacer(Modifier.width(6.dp))
                            Icon(
                                Icons.Default.VolumeUp,
                                contentDescription = "Current track",
                                tint = HomeAccent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    if (index < visibleSongs.lastIndex) Spacer(Modifier.height(4.dp))
                }

                if (songs.size > visibleSongs.size) {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "+ ${songs.size - visibleSongs.size} more songs",
                        color = HomeMuted,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                            .clickable(onClick = onViewQueue)
                            .padding(8.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = onViewQueue,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(13.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = HomeAccent)
            ) {
                Icon(Icons.Default.QueueMusic, contentDescription = null, modifier = Modifier.size(19.dp))
                Spacer(Modifier.width(8.dp))
                Text("View full queue")
            }
        }
    }
}

private data class QuickAccessData(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@Composable
private fun QuickAccessTile(item: QuickAccessData, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(142.dp).clickable(onClick = item.onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF151515))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF252525))
                    .padding(12.dp)
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = HomeAccent.copy(alpha = 0.14f),
                    modifier = Modifier.align(Alignment.BottomEnd).size(76.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth().align(Alignment.TopStart),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = HomeText,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Open ${item.title}",
                        tint = HomeMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Box(
                modifier = Modifier.fillMaxWidth().weight(0.45f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF151515))
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = item.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = HomeMuted,
                    maxLines = 2
                )
            }
        }
    }
}