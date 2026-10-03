package com.example.frem.presentation.albums


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frem.data.repository.MusicRepository
import com.example.frem.domain.model.Album
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class AlbumsViewModel(
    repository: MusicRepository
) : ViewModel() {

    val albums: StateFlow<List<Album>> =
        repository.getAllSongs()
            .map { songs ->

                songs
                    .groupBy {
                        "${it.albumId}_${it.album}"
                    }
                    .values
                    .map { albumSongs ->

                        val first = albumSongs.first()

                        Album(
                            id = first.albumId,
                            title = first.album,
                            artist = first.artist,
                            albumArtist = first.albumArtist,
                            year = first.year,
                            artworkUri = first.albumArtworkUri,
                            songs = albumSongs.sortedBy {
                                it.trackNumber
                            }
                        )
                    }
                    .sortedBy {
                        it.title.lowercase()
                    }

            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyList()
            )
}