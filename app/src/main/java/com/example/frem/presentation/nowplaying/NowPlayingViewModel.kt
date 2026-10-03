package com.example.frem.presentation.nowplaying

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import com.example.frem.data.media.FremPlayer
import com.example.frem.domain.model.Song
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class NowPlayingViewModel(
    private val player: FremPlayer
) : ViewModel() {

    val currentSong: StateFlow<Song?> =
        player.currentSong

    val isPlaying: StateFlow<Boolean> =
        player.isPlaying

    val position: StateFlow<Long> =
        player.position

    val duration: StateFlow<Long> =
        player.duration

    val shuffleEnabled: StateFlow<Boolean> =
        player.shuffleEnabled

    val repeatMode: StateFlow<Int> =
        player.repeatMode

    private var positionJob: Job? = null

    fun startPositionUpdates() {

        if (positionJob?.isActive == true) {
            return
        }

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
        } else {
            player.resume()
        }
    }

    fun previous() {
        player.previous()
    }

    fun next() {
        player.next()
    }

    fun seekTo(position: Long) {
        player.seekTo(position)
    }

    fun toggleShuffle() {
        player.toggleShuffle()
    }

    fun toggleRepeat() {
        player.toggleRepeat()
    }

    override fun onCleared() {
        positionJob?.cancel()
        super.onCleared()
    }
}