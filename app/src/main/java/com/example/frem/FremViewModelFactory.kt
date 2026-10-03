package com.example.frem

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.frem.presentation.albums.AlbumsViewModel
import com.example.frem.presentation.artists.ArtistsViewModel
import com.example.frem.presentation.folders.FoldersViewModel
import com.example.frem.presentation.home.HomeViewModel
import com.example.frem.presentation.nowplaying.NowPlayingViewModel
import com.example.frem.presentation.playlists.PlaylistsViewModel
import com.example.frem.presentation.songs.SongsViewModel

class FremViewModelFactory(
    private val application: FremApplication
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(SongsViewModel::class.java)) {
            return SongsViewModel(
                application.musicRepository
            ) as T
        }

        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(
                application.musicRepository
            ) as T
        }

        if (modelClass.isAssignableFrom(AlbumsViewModel::class.java)) {
            return AlbumsViewModel(
                application.musicRepository
            ) as T
        }

        if (modelClass.isAssignableFrom(ArtistsViewModel::class.java)) {
            return ArtistsViewModel(
                application.musicRepository
            ) as T
        }

        if (modelClass.isAssignableFrom(FoldersViewModel::class.java)) {
            return FoldersViewModel(
                application.musicRepository
            ) as T
        }

        if (modelClass.isAssignableFrom(PlaylistsViewModel::class.java)) {
            return PlaylistsViewModel(
                application.playlistRepository
            ) as T
        }

        if (modelClass.isAssignableFrom(NowPlayingViewModel::class.java)) {
            return NowPlayingViewModel(
                application.fremPlayer
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel: ${modelClass.name}"
        )
    }
}