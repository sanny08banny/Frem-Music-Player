package com.example.frem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.frem.domain.model.QueueSourceType
import com.example.frem.navigation.FremBottomNavigation
import com.example.frem.navigation.FremDestination
import com.example.frem.presentation.albums.AlbumsScreen
import com.example.frem.presentation.albums.AlbumsViewModel
import com.example.frem.presentation.artists.ArtistsScreen
import com.example.frem.presentation.artists.ArtistsViewModel
import com.example.frem.presentation.folders.FoldersScreen
import com.example.frem.presentation.folders.FoldersViewModel
import com.example.frem.presentation.home.HomeScreen
import com.example.frem.presentation.home.HomeViewModel
import com.example.frem.presentation.library.LibraryScreen
import com.example.frem.presentation.nowplaying.NowPlayingScreen
import com.example.frem.presentation.nowplaying.NowPlayingViewModel
import com.example.frem.presentation.playlists.PlaylistsScreen
import com.example.frem.presentation.playlists.PlaylistsViewModel
import com.example.frem.presentation.songs.SongsScreen
import com.example.frem.presentation.songs.SongsViewModel

@Composable
fun FremRoot() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val application = context.applicationContext as FremApplication

    val factory = remember {
        FremViewModelFactory(application)
    }

    val currentRoute = navController
        .currentBackStackEntryAsState()
        .value
        ?.destination
        ?.route

    val startDestination = remember {
        if (application.fremPlayer.hasQueue()) {
            FremDestination.NowPlaying.route
        } else {
            FremDestination.Music.route
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            containerColor = Color.Transparent,
            bottomBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                ) {
                    FremBottomNavigation(
                        currentRoute = currentRoute,
                        onMusicTab = {
                            navController.navigate(FremDestination.Music.route) {
                                launchSingleTop = true
                            }
                        },
                        onNowPlayingTab = {
                            navController.navigate(FremDestination.NowPlaying.route) {
                                launchSingleTop = true
                            }
                        },
                        onAlbumsTab = {
                            navController.navigate(FremDestination.Albums.route) {
                                launchSingleTop = true
                            }
                        },
                        onArtistsTab = {
                            navController.navigate(FremDestination.Artists.route) {
                                launchSingleTop = true
                            }
                        },
                        onLibraryTab = {
                            navController.navigate(FremDestination.Library.route) {
                                launchSingleTop = true
                            }
                        },
                        onProfileTab = {
                            navController.navigate(FremDestination.Profile.route) {
                                launchSingleTop = true
                            }
                        },
                        onSearchTab = {
                            navController.navigate(FremDestination.Search.route) {
                                launchSingleTop = true
                            }
                        },
                        onMoreMenu = {
                            // More menu will be added later.
                        }
                    )
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier
                    .padding(innerPadding)
                    .safeDrawingPadding()
            ) {
                // MUSIC / HOME
                composable(FremDestination.Music.route) {
                    val viewModel: HomeViewModel = viewModel(factory = factory)

                    HomeScreen(
                        viewModel = viewModel,
                        onSongsClick = {
                            navController.navigate("songs")
                        },
                        onAlbumsClick = {
                            navController.navigate(FremDestination.Albums.route)
                        },
                        onArtistsClick = {
                            navController.navigate(FremDestination.Artists.route)
                        },
                        onFoldersClick = {
                            navController.navigate("folders")
                        },
                        onPlaylistsClick = {
                            navController.navigate("playlists")
                        },
                        onSongClick = { song ->
                            application.fremPlayer.play(song)

                            navController.navigate(FremDestination.NowPlaying.route) {
                                launchSingleTop = true
                            }
                        }
                    )
                }

                // NOW PLAYING
                composable(FremDestination.NowPlaying.route) {
                    val viewModel: NowPlayingViewModel = viewModel(factory = factory)

                    NowPlayingScreen(
                        viewModel = viewModel,
                        onBack = {
                            navController.popBackStack()
                        },
                        onQueueClick = {
                            // Queue screen will be added later.
                        }
                    )
                }

                // ALBUMS
                composable(FremDestination.Albums.route) {
                    val viewModel: AlbumsViewModel = viewModel(factory = factory)

                    AlbumsScreen(
                        viewModel = viewModel,
                        onBack = {
                            navController.popBackStack()
                        },
                        onAlbumClick = { album ->
                            if (album.songs.isNotEmpty()) {
                                application.fremPlayer.createQueue(
                                    name = album.title,
                                    sourceType = QueueSourceType.ALBUM,
                                    songs = album.songs,
                                    startIndex = 0,
                                    shuffle = false,
                                    autoPlay = true
                                )

                                navController.navigate(FremDestination.NowPlaying.route) {
                                    launchSingleTop = true
                                }
                            }
                        }
                    )
                }

                // ARTISTS
                composable(FremDestination.Artists.route) {
                    val viewModel: ArtistsViewModel = viewModel(factory = factory)

                    ArtistsScreen(
                        viewModel = viewModel,
                        onBack = {
                            navController.popBackStack()
                        },
                        onArtistClick = { artist ->
                            if (artist.songs.isNotEmpty()) {
                                application.fremPlayer.createQueue(
                                    name = artist.name,
                                    sourceType = QueueSourceType.ARTIST,
                                    songs = artist.songs,
                                    startIndex = 0,
                                    shuffle = false,
                                    autoPlay = true
                                )

                                navController.navigate(FremDestination.NowPlaying.route) {
                                    launchSingleTop = true
                                }
                            }
                        }
                    )
                }

                // LIBRARY
                composable(FremDestination.Library.route) {
                    LibraryScreen(
                        onSongsClick = {
                            navController.navigate("songs")
                        },
                        onFoldersClick = {
                            navController.navigate("folders")
                        },
                        onPlaylistsClick = {
                            navController.navigate("playlists")
                        }
                    )
                }

                // SONGS
                composable("songs") {
                    val viewModel: SongsViewModel = viewModel(factory = factory)

                    SongsScreen(
                        viewModel = viewModel,
                        onBack = {
                            navController.popBackStack()
                        },
                        onSongClick = { song ->
                            application.fremPlayer.play(song)

                            navController.navigate(FremDestination.NowPlaying.route) {
                                launchSingleTop = true
                            }
                        }
                    )
                }

                // FOLDERS
                composable("folders") {
                    val viewModel: FoldersViewModel = viewModel(factory = factory)

                    FoldersScreen(
                        viewModel = viewModel,
                        onBack = {
                            navController.popBackStack()
                        },
                        onFolderClick = {
                            // Folder detail later.
                        }
                    )
                }

                // PLAYLISTS
                composable("playlists") {
                    val viewModel: PlaylistsViewModel = viewModel(factory = factory)

                    PlaylistsScreen(
                        viewModel = viewModel,
                        onBack = {
                            navController.popBackStack()
                        },
                        onPlaylistClick = {
                            // Playlist detail later.
                        }
                    )
                }

                // PROFILE
                composable(FremDestination.Profile.route) {
                    // Profile screen will be built here.
                }

                // SEARCH
                composable(FremDestination.Search.route) {
                    // Search screen will be built here.
                }
            }
        }
    }
}