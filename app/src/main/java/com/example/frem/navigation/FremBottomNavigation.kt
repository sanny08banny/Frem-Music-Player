package com.example.frem.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Album

@Composable
fun FremBottomNavigation(
    modifier: Modifier = Modifier,
    currentRoute: String?,
    onMusicTab: () -> Unit,
    onNowPlayingTab: () -> Unit,
    onAlbumsTab: () -> Unit,
    onArtistsTab: () -> Unit,
    onLibraryTab: () -> Unit,
    onProfileTab: () -> Unit,
    onSearchTab: () -> Unit,
    onMoreMenu: () -> Unit
) {

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0xFF15171C))
            .border(
                width = 1.dp,
                color = Color(0xFF35383F),
                shape = RoundedCornerShape(28.dp)
            )
            .padding(
                horizontal = 10.dp,
                vertical = 8.dp
            )
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {

            BottomNavItem(
                icon = Icons.Default.MusicNote,
                label = "Music",
                selected =
                    currentRoute == FremDestination.Music.route,
                onClick = onMusicTab
            )

            BottomNavItem(
                icon = Icons.Default.GraphicEq,
                label = "Now Playing",
                selected =
                    currentRoute == FremDestination.NowPlaying.route,
                onClick = onNowPlayingTab
            )

            BottomNavItem(
                icon = Icons.Default.Album,
                label = "Albums",
                selected =
                    currentRoute == FremDestination.Albums.route,
                onClick = onAlbumsTab
            )

            BottomNavItem(
                icon = Icons.Default.Person,
                label = "Artists",
                selected =
                    currentRoute == FremDestination.Artists.route,
                onClick = onArtistsTab
            )

            BottomNavItem(
                icon = Icons.Default.Folder,
                label = "Library",
                selected =
                    currentRoute == FremDestination.Library.route,
                onClick = onLibraryTab
            )

            BottomNavItem(
                icon = Icons.Default.AccountCircle,
                label = "Profile",
                selected =
                    currentRoute == FremDestination.Profile.route,
                onClick = onProfileTab
            )

            BottomNavItem(
                icon = Icons.Default.Search,
                label = "Search",
                selected =
                    currentRoute == FremDestination.Search.route,
                onClick = onSearchTab
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable {
                onClick()
            }
            .padding(
                horizontal = 10.dp,
                vertical = 6.dp
            ),
        contentAlignment = Alignment.Center
    ) {

        if (selected) {

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF24272C))
                    .padding(
                        horizontal = 12.dp,
                        vertical = 8.dp
                    )
            ) {

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = Color.White
                    )

                    Text(
                        text = label,
                        color = Color.White,
                        modifier = Modifier.padding(
                            start = 6.dp
                        )
                    )
                }
            }

        } else {

            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color(0xFF9A9CA3)
            )
        }
    }
}