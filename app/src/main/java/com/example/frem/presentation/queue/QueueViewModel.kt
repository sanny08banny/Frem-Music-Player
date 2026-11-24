
package com.example.frem.presentation.queue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.frem.data.media.FremPlayer
import com.example.frem.domain.model.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class QueueViewModel(
    private val fremPlayer: FremPlayer
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")

    val query = searchQuery

    val queue = fremPlayer.queue
    val activeQueue = fremPlayer.activeQueue
    val currentSong = fremPlayer.currentSong
    val isPlaying = fremPlayer.isPlaying
    val shuffleEnabled = fremPlayer.shuffleEnabled

    val filteredQueue = combine(
        queue,
        searchQuery
    ) { songs, query ->
        if (query.isBlank()) {
            songs
        } else {
            songs.filter { song ->
                song.title.contains(query, ignoreCase = true) ||
                        song.artist.contains(query, ignoreCase = true) ||
                        song.album.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    fun search(value: String) {
        searchQuery.value = value
    }

    fun play(song: Song) {
        fremPlayer.playFromQueue(song)
    }

    fun toggleShuffle() {
        fremPlayer.toggleShuffle()
    }

    fun addSongs(songs: List<Song>) {
        fremPlayer.addToQueue(songs)
    }

    fun removeSong(song: Song) {
        fremPlayer.removeFromQueue(song)
    }

    fun moveSongUp(song: Song) {
        val index = queue.value.indexOfFirst { it.id == song.id }
        if (index > 0) {
            fremPlayer.moveQueueItem(index, index - 1)
        }
    }

    fun moveSongDown(song: Song) {
        val index = queue.value.indexOfFirst { it.id == song.id }
        if (index >= 0 && index < queue.value.lastIndex) {
            fremPlayer.moveQueueItem(index, index + 1)
        }
    }

    fun renameQueue(name: String) {
        fremPlayer.renameQueue(name)
    }

    fun clearQueue() {
        fremPlayer.clearQueue()
    }

    class Factory(
        private val fremPlayer: FremPlayer
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            modelClass: Class<T>
        ): T {
            if (modelClass.isAssignableFrom(QueueViewModel::class.java)) {
                return QueueViewModel(fremPlayer) as T
            }
            throw IllegalArgumentException(
                "Unknown ViewModel class: ${modelClass.name}"
            )
        }
    }
}
