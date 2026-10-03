package com.example.frem.presentation.songs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frem.data.repository.MusicRepository
import com.example.frem.domain.model.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class SongSort {
    TITLE,
    ARTIST,
    ALBUM,
    RECENTLY_PLAYED,
    MOST_PLAYED
}

class SongsViewModel(
    private val repository: MusicRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")

    val searchQuery: StateFlow<String> =
        _searchQuery

    private val _sort = MutableStateFlow(
        SongSort.TITLE
    )

    private val allSongs =
        repository.getAllSongs()

    val songs: StateFlow<List<Song>> =
        combine(
            allSongs,
            _searchQuery,
            _sort
        ) { songs, query, sort ->

            var result = songs

            if (query.isNotBlank()) {
                val search = query.trim().lowercase()

                result = result.filter { song ->
                    song.title.lowercase().contains(search) ||
                            song.artist.lowercase().contains(search) ||
                            song.album.lowercase().contains(search) ||
                            song.albumArtist.lowercase().contains(search)
                }
            }

            when (sort) {

                SongSort.TITLE ->
                    result.sortedBy {
                        it.title.lowercase()
                    }

                SongSort.ARTIST ->
                    result.sortedWith(
                        compareBy(
                            { it.artist.lowercase() },
                            { it.title.lowercase() }
                        )
                    )

                SongSort.ALBUM ->
                    result.sortedWith(
                        compareBy(
                            { it.album.lowercase() },
                            { it.trackNumber }
                        )
                    )

                SongSort.RECENTLY_PLAYED ->
                    result.sortedByDescending {
                        it.lastPlayed ?: 0L
                    }

                SongSort.MOST_PLAYED ->
                    result.sortedWith(
                        compareByDescending<Song> {
                            it.playCount
                        }.thenBy {
                            it.title.lowercase()
                        }
                    )
            }

        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSort(sort: SongSort) {
        _sort.value = sort
    }

    fun toggleFavorite(song: Song) {
        viewModelScope.launch {
            repository.toggleFavorite(song)
        }
    }
}