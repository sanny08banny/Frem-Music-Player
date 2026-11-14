
package com.example.frem.presentation.nowplaying

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frem.data.media.FremPlayer
import com.example.frem.data.repository.MusicRepository
import com.example.frem.domain.model.QueueSourceType
import com.example.frem.domain.model.Song
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class NowPlayingViewModel(
    private val player: FremPlayer,
    private val musicRepository: MusicRepository
) : ViewModel() {

    val currentSong: StateFlow<Song?> = player.currentSong
    val isPlaying: StateFlow<Boolean> = player.isPlaying
    val position: StateFlow<Long> = player.position
    val duration: StateFlow<Long> = player.duration
    val shuffleEnabled: StateFlow<Boolean> = player.shuffleEnabled
    val repeatMode: StateFlow<Int> = player.repeatMode

    private var positionJob: Job? = null
    private var loadingLibrary = false

    fun startPositionUpdates() {
        if (positionJob?.isActive == true) return

        positionJob = viewModelScope.launch {
            while (isActive) {
                player.updatePosition()
                delay(500L)
            }
        }
    }

    fun togglePlayPause() {
        if (isPlaying.value) {
            player.pause()
            return
        }

        if (player.hasQueue()) {
            player.resume()
            return
        }

        // No queue: load actual songs from the device library.
        if (loadingLibrary) return

        viewModelScope.launch {
            loadingLibrary = true

            try {
                val songs = musicRepository.getAllSongs().first()

                if (songs.isNotEmpty() && !player.hasQueue()) {
                    // Use the same sourceType value as your existing
                    // "play all songs" or library queue creation code.
                    player.createQueue(
                        name = "All Songs",
                        sourceType = QueueSourceType.ALL_SONGS,
                        songs = songs,
                        startIndex = 0,
                        shuffle = false,
                        autoPlay = true
                    )
                }
            } finally {
                loadingLibrary = false
            }
        }
    }

    fun previous() = player.previous()

    fun next() = player.next()

    fun seekTo(position: Long) = player.seekTo(position)

    fun toggleShuffle() = player.toggleShuffle()

    fun toggleRepeat() = player.toggleRepeat()

    override fun onCleared() {
        positionJob?.cancel()
        super.onCleared()
    }
}
