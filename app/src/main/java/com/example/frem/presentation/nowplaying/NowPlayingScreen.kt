package com.example.frem.presentation.nowplaying

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

@Composable
fun NowPlayingScreen(
    viewModel: NowPlayingViewModel,
    onBack: () -> Unit,
    onQueueClick: () -> Unit
) {
    val song by viewModel.currentSong.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val position by viewModel.position.collectAsState()
    val duration by viewModel.duration.collectAsState()
    val shuffle by viewModel.shuffleEnabled.collectAsState()
    val repeat by viewModel.repeatMode.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.startPositionUpdates()
    }

    val currentSong = song

    if (currentSong == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF07090E)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Nothing is playing",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )
        }
        return
    }

    NowPlayingContent(
        song = currentSong,
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
    val background = Color(0xFF07090E)
    val surface = Color(0xFF16191F)
    val primaryBlue = Color(0xFF2196F3)

    var sliderPosition by remember { mutableFloatStateOf(0f) }
    var isSeeking by remember { mutableStateOf(false) }

    LaunchedEffect(position, duration, isSeeking) {
        if (!isSeeking && duration > 0)
            sliderPosition = (position.toFloat() / duration.toFloat()).coerceIn(0f, 1f)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 24.dp, end = 24.dp, top = 18.dp, bottom = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Text(
                    text = "Now Playing",
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp),
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )

                IconButton(
                    onClick = onQueue,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.QueueMusic,
                        contentDescription = "Queue",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            AlbumArtwork(
                artworkUri = song.albumArtworkUri,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = song.title.ifBlank { "Unknown title" },
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = song.artist.ifBlank { "Unknown artist" },
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFB7B9BE),
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = song.album.ifBlank { "Unknown album" },
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF858991),
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularActionButton(
                    onClick = {},
                    background = surface
                ) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = Color.White,
                        modifier = Modifier.size(25.dp)
                    )
                }

                CircularActionButton(
                    onClick = {},
                    background = surface
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Song information",
                        tint = Color.White,
                        modifier = Modifier.size(25.dp)
                    )
                }

                CircularActionButton(
                    onClick = onQueue,
                    background = surface
                ) {
                    Icon(
                        imageVector = Icons.Default.QueueMusic,
                        contentDescription = "Queue",
                        tint = Color.White,
                        modifier = Modifier.size(25.dp)
                    )
                }

                CircularActionButton(
                    onClick = {},
                    background = surface
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreHoriz,
                        contentDescription = "More",
                        tint = Color.White,
                        modifier = Modifier.size(25.dp)
                    )
                }

                CircularActionButton(
                    onClick = {},
                    background = surface
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = Color.White,
                        modifier = Modifier.size(25.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Slider(
                value = sliderPosition,
                onValueChange = {
                    isSeeking = true
                    sliderPosition = it
                },
                onValueChangeFinished = {
                    if (duration > 0) {
                        val newPosition = (sliderPosition * duration)
                            .toLong()
                            .coerceIn(0L, duration)

                        isSeeking = false
                        onSeek(newPosition)
                    } else {
                        isSeeking = false
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = primaryBlue,
                    inactiveTrackColor = Color(0xFF383C43),
                    activeTickColor = Color.Transparent,
                    inactiveTickColor = Color.Transparent
                )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatDuration(position),
                    color = Color(0xFF9A9DA4),
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = formatDuration(duration),
                    color = Color(0xFF9A9DA4),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onShuffle,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = "Shuffle",
                        tint = if (shuffle) primaryBlue else Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }

                IconButton(
                    onClick = onPrevious,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Previous",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                PlayPauseButton(
                    isPlaying = isPlaying,
                    onClick = onPlayPause,
                    primaryBlue = primaryBlue
                )

                IconButton(
                    onClick = onNext,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                IconButton(
                    onClick = onRepeat,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.QueueMusic,
                        contentDescription = when (repeat) {
                            Player.REPEAT_MODE_ONE -> "Repeat one"
                            Player.REPEAT_MODE_ALL -> "Repeat all"
                            else -> "Repeat off"
                        },
                        tint = if (repeat != Player.REPEAT_MODE_OFF) primaryBlue else Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AlbumArtwork(
    artworkUri: String?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .background(Color(0xFF14171D))
    ) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(context)
                .data(artworkUri)
                .crossfade(true)
                .build(),
            contentDescription = "Album artwork",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            loading = { ArtworkPlaceholder() },
            error = { ArtworkPlaceholder() }
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.25f)
                        )
                    )
                )
        )
    }
}

@Composable
private fun ArtworkPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF151922),
                        Color(0xFF252B36)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.MusicNote,
            contentDescription = "No artwork",
            tint = Color(0xFF6D7480),
            modifier = Modifier.size(72.dp)
        )
    }
}

@Composable
private fun CircularActionButton(
    onClick: () -> Unit,
    background: Color,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .size(58.dp)
            .clip(CircleShape)
            .background(background)
            .border(
                width = 1.dp,
                color = Color(0xFF353941),
                shape = CircleShape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
private fun PlayPauseButton(
    isPlaying: Boolean,
    onClick: () -> Unit,
    primaryBlue: Color
) {
    Box(
        modifier = Modifier
            .size(82.dp)
            .shadow(
                elevation = 14.dp,
                shape = CircleShape,
                ambientColor = primaryBlue,
                spotColor = primaryBlue
            )
            .clip(CircleShape)
            .background(Color(0xFF10141B))
            .border(
                width = 3.dp,
                color = primaryBlue,
                shape = CircleShape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = if (isPlaying) "Pause" else "Play",
            tint = Color.White,
            modifier = Modifier.size(if (isPlaying) 42.dp else 46.dp)
        )
    }
}

private fun formatDuration(milliseconds: Long): String {
    if (milliseconds <= 0L)
        return "0:00"

    val totalSeconds = milliseconds / 1000L
    val hours = totalSeconds / 3600L
    val minutes = (totalSeconds % 3600L) / 60L
    val seconds = totalSeconds % 60L

    return if (hours > 0L) {
        "%d:%02d:%02d".format(hours, minutes, seconds)
    } else {
        "%d:%02d".format(minutes, seconds)
    }
}