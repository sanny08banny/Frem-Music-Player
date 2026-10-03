package com.example.frem.presentation.artists


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frem.data.repository.MusicRepository
import com.example.frem.domain.model.Album
import com.example.frem.domain.model.Artist
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ArtistsViewModel(
    repository: MusicRepository
) : ViewModel() {

    val artists: StateFlow<List<Artist>> =
        repository.getAllSongs()
            .map { songs ->

                songs
                    .groupBy {
                        it.artist
                    }
                    .map { (name, artistSongs) ->

                        val albums = artistSongs
                            .groupBy {
                                it.albumId
                            }
                            .map { (_, albumSongs) ->

                                val first = albumSongs.first()

                                Album(
                                    id = first.albumId,
                                    title = first.album,
                                    artist = first.artist,
                                    albumArtist = first.albumArtist,
                                    year = first.year,
                                    artworkUri = first.albumArtworkUri,
                                    songs = albumSongs
                                )
                            }

                        Artist(
                            id = name.hashCode().toLong(),
                            name = name,
                            albumCount = albums.size,
                            songCount = artistSongs.size,
                            albums = albums,
                            songs = artistSongs
                        )
                    }
                    .sortedBy {
                        it.name.lowercase()
                    }

            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyList()
            )
}