
package com.example.frem.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val ProfileBackground = Color(0xFF090B10)
private val ProfileSurface = Color(0xFF171B23)
private val ProfileSurfaceVariant = Color(0xFF222B39)
private val ProfileBlue = Color(0xFF4A9EFF)
private val ProfileLime = Color(0xFFB7F36B)
private val ProfileText = Color(0xFFF5F7FA)
private val ProfileSecondary = Color(0xFFB0B7C3)
private val ProfileMuted = Color(0xFF818A99)
private val ProfileDivider = Color(0xFF303744)

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    appVersion: String = "1.0.0",
    onAudioSettingsClick: () -> Unit = {},
    onSleepTimerClick: () -> Unit = {},
    onMusicLibraryClick: () -> Unit = {},
    onAppearanceClick: () -> Unit = {},
    onBackupClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    onAboutClick: () -> Unit = {}
) {
    // Existing player state from ProfileViewModel.
    val currentSong by viewModel.currentSong.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val position by viewModel.position.collectAsState()
    val duration by viewModel.duration.collectAsState()
    val shuffleEnabled by viewModel.shuffleEnabled.collectAsState()
    val repeatMode by viewModel.repeatMode.collectAsState()

    // Local settings state. These are UI-only until persistence
    // and player integration are implemented.
    var gaplessPlayback by remember { mutableStateOf(true) }
    var resumeWithHeadphones by remember { mutableStateOf(true) }
    var pauseOnAudioFocusLoss by remember { mutableStateOf(true) }
    var showAlbumArtwork by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ProfileBackground)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF172438).copy(alpha = 0.72f),
                            Color(0xFF101722).copy(alpha = 0.35f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp)
        ) {
            Spacer(modifier = Modifier.height(28.dp))

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(7.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(ProfileLime)
                        )

                        Text(
                            text = "FREM MUSIC PLAYER",
                            color = ProfileMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Your space.",
                        color = ProfileText,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-1).sp
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = "Make your listening experience yours.",
                        color = ProfileSecondary,
                        fontSize = 13.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(ProfileSurfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = null,
                        tint = ProfileBlue,
                        modifier = Modifier.size(27.dp)
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(10.dp)
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(ProfileLime)
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Playback overview
            ProfileSectionHeader(
                title = "PLAYBACK",
                number = "01"
            )

            ProfileNavigationRow(
                icon = Icons.Default.Equalizer,
                title = "Audio settings",
                subtitle = "Equalizer and sound controls",
                iconColor = ProfileBlue,
                onClick = onAudioSettingsClick
            )

            ProfileDivider()

            ProfileNavigationRow(
                icon = Icons.Default.Timer,
                title = "Sleep timer",
                subtitle = "Set a time to stop playback",
                iconColor = ProfileLime,
                onClick = onSleepTimerClick
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Listening experience
            ProfileSectionHeader(
                title = "LISTENING EXPERIENCE",
                number = "02"
            )

            ProfileSwitchRow(
                icon = Icons.Default.GraphicEq,
                title = "Gapless playback",
                subtitle = "Play tracks without pauses",
                checked = gaplessPlayback,
                iconColor = ProfileLime,
                onCheckedChange = { gaplessPlayback = it }
            )

            ProfileDivider()

            ProfileSwitchRow(
                icon = Icons.Default.Headphones,
                title = "Resume with headphones",
                subtitle = "Continue when headphones reconnect",
                checked = resumeWithHeadphones,
                iconColor = ProfileBlue,
                onCheckedChange = { resumeWithHeadphones = it }
            )

            ProfileDivider()

            ProfileSwitchRow(
                icon = Icons.Default.VolumeOff,
                title = "Audio focus",
                subtitle = "Pause when another app takes audio focus",
                checked = pauseOnAudioFocusLoss,
                iconColor = ProfileBlue,
                onCheckedChange = { pauseOnAudioFocusLoss = it }
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Library and appearance
            ProfileSectionHeader(
                title = "LIBRARY & APPEARANCE",
                number = "03"
            )

            ProfileNavigationRow(
                icon = Icons.Default.LibraryMusic,
                title = "Music library",
                subtitle = "Manage your local music collection",
                iconColor = ProfileBlue,
                onClick = onMusicLibraryClick
            )

            ProfileDivider()

            ProfileNavigationRow(
                icon = Icons.Default.Palette,
                title = "Appearance",
                subtitle = "Personalise the look of Frem",
                iconColor = ProfileLime,
                onClick = onAppearanceClick
            )

            ProfileDivider()

            ProfileSwitchRow(
                icon = Icons.Default.Album,
                title = "Show album artwork",
                subtitle = "Display artwork throughout the app",
                checked = showAlbumArtwork,
                iconColor = ProfileBlue,
                onCheckedChange = { showAlbumArtwork = it }
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Application
            ProfileSectionHeader(
                title = "DATA & APPLICATION",
                number = "04"
            )

            ProfileNavigationRow(
                icon = Icons.Default.Backup,
                title = "Backup & restore",
                subtitle = "Keep your music settings safe",
                iconColor = ProfileBlue,
                onClick = onBackupClick
            )

            ProfileDivider()

            ProfileNavigationRow(
                icon = Icons.Default.Share,
                title = "Share Frem",
                subtitle = "Share the app with others",
                iconColor = ProfileLime,
                onClick = onShareClick
            )

            ProfileDivider()

            ProfileNavigationRow(
                icon = Icons.Default.Info,
                title = "About Frem",
                subtitle = "App information and version",
                trailing = appVersion,
                iconColor = ProfileBlue,
                onClick = onAboutClick
            )

            Spacer(modifier = Modifier.height(34.dp))

            // Footer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 28.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "FREM",
                    color = ProfileMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 3.sp
                )

                Spacer(modifier = Modifier.size(8.dp))

                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(ProfileLime)
                )

                Spacer(modifier = Modifier.size(8.dp))

                Text(
                    text = "VERSION $appVersion",
                    color = ProfileMuted,
                    fontSize = 9.sp,
                    letterSpacing = 1.2.sp
                )
            }
        }
    }
}

@Composable
private fun ProfileSectionHeader(
    title: String,
    number: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = number,
            color = ProfileBlue,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.size(11.dp))

        Text(
            text = title,
            color = ProfileMuted,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.6.sp
        )

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .size(5.dp)
                .clip(CircleShape)
                .background(ProfileDivider)
        )
    }
}

@Composable
private fun ProfileNavigationRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconColor: Color = ProfileBlue,
    trailing: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(ProfileSurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.size(14.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                color = ProfileText,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                color = ProfileMuted,
                fontSize = 11.sp
            )
        }

        if (trailing != null) {
            Text(
                text = trailing,
                color = ProfileMuted,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.size(8.dp))
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = ProfileMuted,
            modifier = Modifier.size(19.dp)
        )
    }
}

@Composable
private fun ProfileSwitchRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    iconColor: Color = ProfileBlue,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(ProfileSurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.size(14.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                color = ProfileText,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                color = ProfileMuted,
                fontSize = 11.sp
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = ProfileBackground,
                checkedTrackColor = ProfileLime,
                checkedBorderColor = ProfileLime,
                uncheckedThumbColor = ProfileMuted,
                uncheckedTrackColor = ProfileSurfaceVariant,
                uncheckedBorderColor = ProfileDivider
            )
        )
    }
}

@Composable
private fun ProfileDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 56.dp)
            .height(1.dp)
            .background(ProfileDivider.copy(alpha = 0.55f))
    )
}
