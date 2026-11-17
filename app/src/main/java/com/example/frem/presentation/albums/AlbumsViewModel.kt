
package com.example.frem.presentation.albums

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frem.data.repository.MusicRepository
import com.example.frem.domain.model.Album
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AlbumsViewModel(
    private val repository: MusicRepository
) : ViewModel() {

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _refreshError = MutableStateFlow<String?>(null)
    val refreshError = _refreshError.asStateFlow()

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

    fun refreshAlbums() {
        if (_isRefreshing.value) return

        viewModelScope.launch {
            _isRefreshing.value = true
            _refreshError.value = null

            try {
                repository.refreshSongs()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _refreshError.value =
                    e.message ?: "Unable to refresh music library"
            } finally {
                _isRefreshing.value = false
            }
        }
    }
}
