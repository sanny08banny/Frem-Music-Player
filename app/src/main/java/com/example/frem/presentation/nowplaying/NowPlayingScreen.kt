package com.example.frem.presentation.nowplaying

import android.content.ClipData
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.frem.domain.model.Song
import java.util.Locale

private val ScreenBackground = Color(0xFF090B10)
private val SurfaceColor = Color(0xFF171B23)
private val PrimaryBlue = Color(0xFF4A9EFF)
private val PrimaryText = Color(0xFFF3F5F8)
private val SecondaryText = Color(0xFFABB3C0)
private val MutedText = Color(0xFF747F90)
private val BorderColor = Color(0xFF29313E)

private val EmptySong = Song(
    id = -1L, title = "No song selected", artist = "Choose a song to get started",
    album = "Your music library", albumArtist = "", duration = 0L, trackNumber = 0,
    year = 0, genre = "", contentUri = "", path = "", mimeType = "", size = 0L,
    dateAdded = 0L, dateModified = 0L, albumId = -1L, albumArtworkUri = null,
    isFavorite = false, playCount = 0, lastPlayed = null, dateAddedToLibrary = 0L
)

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

    LaunchedEffect(Unit) { viewModel.startPositionUpdates() }

    NowPlayingContent(
        song = song ?: EmptySong,
        isPlaying = isPlaying,
        position = position,
        duration = duration,
        shuffle = shuffle,
        repeat = repeat,
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
    onQueue: () -> Unit,
    onPlayPause: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onSeek: (Long) -> Unit,
    onShuffle: () -> Unit,
    onRepeat: () -> Unit
) {
    val context = LocalContext.current
    var sliderPosition by remember { mutableFloatStateOf(0f) }
    var isSeeking by remember { mutableStateOf(false) }
    var showSongInfo by remember { mutableStateOf(false) }
    var artworkColor by remember { mutableStateOf(PrimaryBlue) }

    LaunchedEffect(song.albumArtworkUri) {
        artworkColor = extractArtworkColor(context, song.albumArtworkUri)
    }

    LaunchedEffect(position, duration, isSeeking) {
        if (!isSeeking && duration > 0L) {
            sliderPosition = (position.toFloat() / duration.toFloat()).coerceIn(0f, 1f)
        }
        if (duration <= 0L && !isSeeking) sliderPosition = 0f
    }

    val accentColor = artworkColor
    val accentGlow = accentColor.copy(alpha = 0.16f)

    Box(
        modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(
                colors = listOf(
                    accentColor.copy(alpha = 0.20f),
                    Color(0xFF111722),
                    ScreenBackground,
                    ScreenBackground
                )
            )
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()
                .padding(horizontal = 26.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            AlbumArtwork(
                artworkUri = song.albumArtworkUri,
                modifier = Modifier.fillMaxWidth().aspectRatio(1f)
            )

            Spacer(modifier = Modifier.height(27.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = song.title.ifBlank { "No song selected" },
                    color = PrimaryText,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(7.dp))

                Text(
                    text = song.artist.ifBlank { "Choose a song to get started" },
                    color = SecondaryText,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = song.album.ifBlank { "Your music library" },
                    color = MutedText,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(17.dp))

            Row(
                modifier = Modifier.fillMaxWidth().height(48.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SecondaryAction(Icons.Default.FavoriteBorder, "Favorite", onClick = {})
                SecondaryAction(Icons.Default.Info, "Song information") { showSongInfo = true }
                SecondaryAction(Icons.Default.QueueMusic, "Queue", onQueue)
                SecondaryAction(Icons.Default.Share, "Share") { shareSong(context, song) }
                SecondaryAction(Icons.Default.MoreHoriz, "More options", onClick = {})
            }

            Spacer(modifier = Modifier.height(19.dp))

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
                modifier = Modifier.fillMaxWidth().height(28.dp),
                colors = SliderDefaults.colors(
                    thumbColor = PrimaryText,
                    activeTrackColor = accentColor,
                    inactiveTrackColor = Color(0xFF343C49),
                    activeTickColor = Color.Transparent,
                    inactiveTickColor = Color.Transparent
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 3.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatDuration(
                        if (isSeeking && duration > 0L) (sliderPosition * duration).toLong()
                        else position
                    ),
                    color = SecondaryText,
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    text = formatDuration(duration),
                    color = MutedText,
                    style = MaterialTheme.typography.labelMedium
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onShuffle, modifier = Modifier.size(44.dp)) {
                    Icon(
                        Icons.Default.Shuffle,
                        contentDescription = "Shuffle",
                        tint = if (shuffle) accentColor else MutedText,
                        modifier = Modifier.size(23.dp)
                    )
                }

                IconButton(onClick = onPrevious, modifier = Modifier.size(48.dp)) {
                    Icon(
                        Icons.Default.SkipPrevious,
                        contentDescription = "Previous",
                        tint = PrimaryText,
                        modifier = Modifier.size(34.dp)
                    )
                }

                PlayPauseButton(isPlaying, onPlayPause, accentColor, accentGlow)

                IconButton(onClick = onNext, modifier = Modifier.size(48.dp)) {
                    Icon(
                        Icons.Default.SkipNext,
                        contentDescription = "Next",
                        tint = PrimaryText,
                        modifier = Modifier.size(34.dp)
                    )
                }

                IconButton(onClick = onRepeat, modifier = Modifier.size(44.dp)) {
                    Icon(
                        imageVector = when (repeat) {
                            Player.REPEAT_MODE_ONE -> Icons.Default.RepeatOne
                            else -> Icons.Default.Repeat
                        },
                        contentDescription = when (repeat) {
                            Player.REPEAT_MODE_ONE -> "Repeat one"
                            Player.REPEAT_MODE_ALL -> "Repeat all"
                            else -> "Repeat off"
                        },
                        tint = if (repeat != Player.REPEAT_MODE_OFF) accentColor else MutedText,
                        modifier = Modifier.size(23.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }

    if (showSongInfo) {
        SongInformationDialog(
            song = song,
            accentColor = accentColor,
            onDismiss = { showSongInfo = false }
        )
    }
}

@Composable
private fun AlbumArtwork(
    artworkUri: String?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Box(
        modifier = modifier.shadow(
            elevation = 18.dp,
            shape = RoundedCornerShape(18.dp),
            ambientColor = Color.Black.copy(alpha = 0.25f),
            spotColor = Color.Black.copy(alpha = 0.35f)
        ).clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF171D28))
            .border(1.dp, Color.White.copy(alpha = 0.09f), RoundedCornerShape(18.dp))
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
            modifier = Modifier.fillMaxWidth().height(65.dp).align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.16f))
                    )
                )
        )
    }
}

@Composable
private fun ArtworkPlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(
            Brush.linearGradient(
                listOf(Color(0xFF26354A), Color(0xFF1C2637), Color(0xFF111722))
            )
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
            Icon(
                Icons.Default.MusicNote,
                contentDescription = "Default album artwork",
                tint = Color(0xFFB9CDE8),
                modifier = Modifier.size(76.dp)
            )
        }
    }
}

private suspend fun extractArtworkColor(
    context: android.content.Context,
    artworkUri: String?
): Color {
    if (artworkUri.isNullOrBlank()) return PrimaryBlue

    return withContext(Dispatchers.IO) {
        try {
            val uri = Uri.parse(artworkUri)
            val bitmap = context.contentResolver.openInputStream(uri)?.use { stream ->
                val options = BitmapFactory.Options().apply {
                    inSampleSize = 8
                    inPreferredConfig = android.graphics.Bitmap.Config.RGB_565
                }
                BitmapFactory.decodeStream(stream, null, options)
            } ?: return@withContext PrimaryBlue

            var bestColor = PrimaryBlue
            var bestScore = -1f
            val step = 4

            for (x in 0 until bitmap.width step step) {
                for (y in 0 until bitmap.height step step) {
                    val pixel = bitmap.getPixel(x, y)
                    val red = android.graphics.Color.red(pixel)
                    val green = android.graphics.Color.green(pixel)
                    val blue = android.graphics.Color.blue(pixel)
                    val hsv = FloatArray(3)

                    android.graphics.Color.RGBToHSV(red, green, blue, hsv)

                    val saturation = hsv[1]
                    val brightness = hsv[2]

                    if (saturation < 0.22f || brightness < 0.20f || brightness > 0.95f) continue

                    val score = saturation * (1f - kotlin.math.abs(brightness - 0.55f))

                    if (score > bestScore) {
                        bestScore = score
                        bestColor = Color(red / 255f, green / 255f, blue / 255f)
                    }
                }
            }

            bitmap.recycle()
            if (bestScore < 0f) PrimaryBlue else bestColor
        } catch (e: Exception) {
            PrimaryBlue
        }
    }
}

@Composable
private fun SecondaryAction(
    icon: ImageVector,
    description: String,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick, modifier = Modifier.size(44.dp)) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            tint = SecondaryText,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
private fun PlayPauseButton(
    isPlaying: Boolean,
    onClick: () -> Unit,
    accentColor: Color,
    accentGlow: Color
) {
    Box(
        modifier = Modifier.size(68.dp).shadow(
            elevation = 12.dp,
            shape = CircleShape,
            ambientColor = accentGlow,
            spotColor = accentGlow
        ).clip(CircleShape).background(
            Brush.linearGradient(
                listOf(
                    accentColor.copy(alpha = 0.95f),
                    accentColor.copy(alpha = 0.72f)
                )
            )
        ).clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = if (isPlaying) "Pause" else "Play",
            tint = Color.White,
            modifier = Modifier.size(if (isPlaying) 34.dp else 38.dp)
        )
    }
}

@Composable
private fun SongInformationDialog(
    song: Song,
    accentColor: Color,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF171C25),
        shape = RoundedCornerShape(22.dp),
        title = {
            Column {
                Text(
                    text = "Song information",
                    color = PrimaryText,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = "Track details and metadata",
                    color = MutedText,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                SongInfoRow("Title", song.title)
                SongInfoRow("Artist", song.artist)
                SongInfoRow("Album", song.album)
                SongInfoRow("Album artist", song.albumArtist)
                SongInfoRow("Genre", song.genre)
                SongInfoRow("Track number", song.trackNumber.takeIf { it > 0 }?.toString())
                SongInfoRow("Year", song.year.takeIf { it > 0 }?.toString())
                SongInfoRow("Duration", formatDuration(song.duration))
                SongInfoRow("File size", formatFileSize(song.size))
                SongInfoRow("File path", song.path)
                SongInfoRow("Format", song.mimeType)
                SongInfoRow("Play count", song.playCount.toString())
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done", color = accentColor, fontWeight = FontWeight.SemiBold)
            }
        }
    )
}

@Composable
private fun SongInfoRow(label: String, value: String?) {
    if (value.isNullOrBlank()) return

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label.uppercase(Locale.getDefault()),
            color = MutedText,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(text = value, color = PrimaryText, style = MaterialTheme.typography.bodyMedium)
    }
}

private fun shareSong(context: android.content.Context, song: Song) {
    if (song.id < 0L) return

    val shareText = buildString {
        append(song.title)
        if (song.artist.isNotBlank()) {
            append(" • ")
            append(song.artist)
        }
        if (song.album.isNotBlank()) {
            append("\nAlbum: ")
            append(song.album)
        }
    }

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = song.mimeType.takeIf {
            it.isNotBlank() && it != "application/octet-stream"
        } ?: "audio/*"

        putExtra(Intent.EXTRA_SUBJECT, song.title)
        putExtra(Intent.EXTRA_TEXT, shareText)

        if (song.contentUri.isNotBlank()) {
            val uri = Uri.parse(song.contentUri)
            putExtra(Intent.EXTRA_STREAM, uri)
            clipData = ClipData.newUri(context.contentResolver, song.title, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    try {
        context.startActivity(Intent.createChooser(intent, "Share song"))
    } catch (e: Exception) {
        val fallback = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        context.startActivity(Intent.createChooser(fallback, "Share song"))
    }
}

private fun formatFileSize(bytes: Long): String {
    if (bytes <= 0L) return "Unknown"
    val mb = bytes / (1024.0 * 1024.0)

    return if (mb >= 1024.0) {
        "%.2f GB".format(Locale.getDefault(), mb / 1024.0)
    } else {
        "%.2f MB".format(Locale.getDefault(), mb)
    }
}

private fun formatDuration(milliseconds: Long): String {
    if (milliseconds <= 0L) return "0:00"

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