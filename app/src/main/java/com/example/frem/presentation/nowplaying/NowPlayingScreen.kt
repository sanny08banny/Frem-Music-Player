package com.example.frem.presentation.nowplaying

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.frem.domain.model.Song

private val ScreenBackground = Color(0xFF090B10)
private val SurfaceColor = Color(0xFF171B23)
private val PrimaryBlue = Color(0xFF4A9EFF)
private val PrimaryText = Color(0xFFF5F7FA)
private val SecondaryText = Color(0xFFB0B7C3)
private val MutedText = Color(0xFF818A99)


private val EmptySong = Song(
    id = -1L,
    title = "No song selected",
    artist = "Choose a song to get started",
    album = "Your music library",
    albumArtist = "",
    duration = 0L,
    trackNumber = 0,
    year = 0,
    genre = "",
    contentUri = "",
    path = "",
    mimeType = "",
    size = 0L,
    dateAdded = 0L,
    dateModified = 0L,
    albumId = -1L,
    albumArtworkUri = null,
    isFavorite = false,
    playCount = 0,
    lastPlayed = null,
    dateAddedToLibrary = 0L
)

@Composable
fun NowPlayingScreen(viewModel: NowPlayingViewModel, onBack: () -> Unit, onQueueClick: () -> Unit) {
    val song by viewModel.currentSong.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val position by viewModel.position.collectAsState()
    val duration by viewModel.duration.collectAsState()
    val shuffle by viewModel.shuffleEnabled.collectAsState()
    val repeat by viewModel.repeatMode.collectAsState()

    LaunchedEffect(Unit) { viewModel.startPositionUpdates() }

    NowPlayingContent(
        song = song ?: EmptySong,
        isPlaying = isPlaying,
        position = position,
        duration = duration,
        shuffle = shuffle,
        repeat = repeat,
        onBack = onBack,
        onQueue = onQueueClick,
        onPlayPause = viewModel::togglePlayPause,
        onPrevious = viewModel::previous,
        onNext = viewModel::next,
        onSeek = viewModel::seekTo,
        onShuffle = viewModel::toggleShuffle,
        onRepeat = viewModel::toggleRepeat
    )
}

@Composable
private fun NowPlayingContent(
    song: Song,
    isPlaying: Boolean,
    position: Long,
    duration: Long,
    shuffle: Boolean,
    repeat: Int,
    onBack: () -> Unit,
    onQueue: () -> Unit,
    onPlayPause: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onSeek: (Long) -> Unit,
    onShuffle: () -> Unit,
    onRepeat: () -> Unit
) {
    var sliderPosition by remember { mutableFloatStateOf(0f) }
    var isSeeking by remember { mutableStateOf(false) }

    LaunchedEffect(position, duration, isSeeking) {
        if (!isSeeking && duration > 0L) {
            sliderPosition = (position.toFloat() / duration.toFloat()).coerceIn(0f, 1f)
        }
        if (duration <= 0L && !isSeeking) sliderPosition = 0f
    }

    Box(
        modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color(0xFF141A26), Color(0xFF0D1119), ScreenBackground))
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(start = 24.dp, end = 24.dp, top = 18.dp, bottom = 24.dp)
        ) {
//            IconButton(onClick = onQueue, modifier = Modifier.size(48.dp)) {
//                Icon(Icons.Default.QueueMusic, contentDescription = "Queue", tint = SecondaryText, modifier = Modifier.size(26.dp))
//            }
            Spacer(Modifier.height(14.dp))

            AlbumArtwork(song.albumArtworkUri, Modifier.fillMaxWidth().aspectRatio(1f))

            Spacer(Modifier.height(24.dp))

            Text(
                song.title.ifBlank { "No song selected" },
                modifier = Modifier.fillMaxWidth(),
                color = PrimaryText,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2
            )

            Spacer(Modifier.height(5.dp))

            Text(
                song.artist.ifBlank { "Choose a song to get started" },
                modifier = Modifier.fillMaxWidth(),
                color = SecondaryText,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )

            Spacer(Modifier.height(4.dp))

            Text(
                song.album.ifBlank { "Your music library" },
                modifier = Modifier.fillMaxWidth(),
                color = MutedText,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1
            )

            Spacer(Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularActionButton({}, SurfaceColor) {
                    Icon(Icons.Default.FavoriteBorder, contentDescription = "Favorite", tint = SecondaryText, modifier = Modifier.size(24.dp))
                }
                CircularActionButton({}, SurfaceColor) {
                    Icon(Icons.Default.Info, contentDescription = "Song information", tint = SecondaryText, modifier = Modifier.size(24.dp))
                }
                CircularActionButton(onQueue, SurfaceColor) {
                    Icon(Icons.Default.QueueMusic, contentDescription = "Queue", tint = SecondaryText, modifier = Modifier.size(24.dp))
                }
                CircularActionButton({}, SurfaceColor) {
                    Icon(Icons.Default.MoreHoriz, contentDescription = "More", tint = SecondaryText, modifier = Modifier.size(24.dp))
                }
                CircularActionButton({}, SurfaceColor) {
                    Icon(Icons.Default.QueueMusic, contentDescription = "Queue", tint = SecondaryText, modifier = Modifier.size(24.dp))
                }
                CircularActionButton({}, SurfaceColor) {
                    Icon(Icons.Default.Share, contentDescription = "Share", tint = SecondaryText, modifier = Modifier.size(24.dp))
                }
            }

            Spacer(Modifier.height(16.dp))

            Slider(
                value = sliderPosition,
                onValueChange = {
                    isSeeking = true
                    sliderPosition = it
                },
                onValueChangeFinished = {
                    if (duration > 0L) {
                        onSeek((sliderPosition * duration).toLong().coerceIn(0L, duration))
                    }
                    isSeeking = false
                },
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = PrimaryText,
                    activeTrackColor = PrimaryBlue,
                    inactiveTrackColor = Color(0xFF343B48),
                    activeTickColor = Color.Transparent,
                    inactiveTickColor = Color.Transparent
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    formatDuration(if (isSeeking && duration > 0L) (sliderPosition * duration).toLong() else position),
                    color = MutedText,
                    style = MaterialTheme.typography.labelMedium
                )
                Text(formatDuration(duration), color = MutedText, style = MaterialTheme.typography.labelMedium)
            }

            Spacer(Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onShuffle, modifier = Modifier.size(56.dp)) {
                    Icon(
                        Icons.Default.GraphicEq,
                        contentDescription = "Shuffle",
                        tint = if (shuffle) PrimaryBlue else SecondaryText,
                        modifier = Modifier.size(28.dp)
                    )
                }
                IconButton(onClick = onPrevious, modifier = Modifier.size(56.dp)) {
                    Icon(Icons.Default.SkipPrevious, contentDescription = "Previous", tint = PrimaryText, modifier = Modifier.size(36.dp))
                }
                PlayPauseButton(isPlaying, onPlayPause, PrimaryBlue)
                IconButton(onClick = onNext, modifier = Modifier.size(56.dp)) {
                    Icon(Icons.Default.SkipNext, contentDescription = "Next", tint = PrimaryText, modifier = Modifier.size(36.dp))
                }
                IconButton(onClick = onRepeat, modifier = Modifier.size(56.dp)) {
                    Icon(
                        Icons.Default.QueueMusic,
                        contentDescription = when (repeat) {
                            Player.REPEAT_MODE_ONE -> "Repeat one"
                            Player.REPEAT_MODE_ALL -> "Repeat all"
                            else -> "Repeat off"
                        },
                        tint = if (repeat != Player.REPEAT_MODE_OFF) PrimaryBlue else SecondaryText,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AlbumArtwork(artworkUri: String?, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Box(
        modifier = modifier.shadow(
            elevation = 12.dp,
            shape = RoundedCornerShape(22.dp),
            ambientColor = Color.Black.copy(alpha = 0.25f),
            spotColor = Color.Black.copy(alpha = 0.35f)
        ).clip(RoundedCornerShape(22.dp)).background(Color(0xFF171D28))
    ) {
        if (artworkUri.isNullOrBlank()) {
            ArtworkPlaceholder(Modifier.fillMaxSize())
        } else {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(context).data(artworkUri).crossfade(true).build(),
                contentDescription = "Album artwork",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                loading = { ArtworkPlaceholder(Modifier.fillMaxSize()) },
                error = { ArtworkPlaceholder(Modifier.fillMaxSize()) }
            )
        }

        Box(
            modifier = Modifier.fillMaxWidth().height(80.dp).align(Alignment.BottomCenter).background(
                Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.12f), Color.Black.copy(alpha = 0.38f)))
            )
        )
    }
}

@Composable
private fun ArtworkPlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(
            Brush.linearGradient(listOf(Color(0xFF26354A), Color(0xFF1C2637), Color(0xFF111722)))
        ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.size(144.dp).clip(CircleShape).background(
                Brush.radialGradient(
                    listOf(
                        Color(0xFF526D94).copy(alpha = 0.36f),
                        Color(0xFF30415B).copy(alpha = 0.18f),
                        Color.Transparent
                    )
                )
            ),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.MusicNote, contentDescription = "Default album artwork", tint = Color(0xFFB9CDE8), modifier = Modifier.size(76.dp))
        }
    }
}

@Composable
private fun CircularActionButton(onClick: () -> Unit, background: Color, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier.size(54.dp).clip(CircleShape).background(background).border(1.dp, Color(0xFF303744), CircleShape).clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
private fun PlayPauseButton(isPlaying: Boolean, onClick: () -> Unit, primaryBlue: Color) {
    Box(
        modifier = Modifier.size(80.dp).shadow(
            elevation = 12.dp,
            shape = CircleShape,
            ambientColor = primaryBlue.copy(alpha = 0.35f),
            spotColor = primaryBlue.copy(alpha = 0.30f)
        ).clip(CircleShape).background(
            Brush.linearGradient(listOf(Color(0xFF1C2636), Color(0xFF111722)))
        ).border(2.dp, primaryBlue, CircleShape).clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = if (isPlaying) "Pause" else "Play",
            tint = PrimaryText,
            modifier = Modifier.size(if (isPlaying) 40.dp else 44.dp)
        )
    }
}

private fun formatDuration(milliseconds: Long): String {
    if (milliseconds <= 0L) return "0:00"
    val totalSeconds = milliseconds / 1000L
    val hours = totalSeconds / 3600L
    val minutes = (totalSeconds % 3600L) / 60L
    val seconds = totalSeconds % 60L
    return if (hours > 0L) "%d:%02d:%02d".format(hours, minutes, seconds)
    else "%d:%02d".format(minutes, seconds)
}