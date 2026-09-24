package com.example.frem.data.media


import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.frem.domain.model.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FremPlayer(
    context: Context
) {

    private val player: ExoPlayer = ExoPlayer.Builder(context)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                .build(),
            true
        )
        .build()

    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _playbackState = MutableStateFlow(Player.STATE_IDLE)
    val playbackState: StateFlow<Int> = _playbackState.asStateFlow()

    private val _position = MutableStateFlow(0L)
    val position: StateFlow<Long> = _position.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration.asStateFlow()

    private val _queue = MutableStateFlow<List<Song>>(emptyList())
    val queue: StateFlow<List<Song>> = _queue.asStateFlow()

    private val _shuffleEnabled = MutableStateFlow(false)
    val shuffleEnabled: StateFlow<Boolean> =
        _shuffleEnabled.asStateFlow()

    private val _repeatMode = MutableStateFlow(Player.REPEAT_MODE_OFF)
    val repeatMode: StateFlow<Int> = _repeatMode.asStateFlow()

    private var currentIndex = -1

    init {
        player.addListener(
            object : Player.Listener {

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _isPlaying.value = isPlaying
                }

                override fun onPlaybackStateChanged(state: Int) {
                    _playbackState.value = state

                    if (state == Player.STATE_READY) {
                        _duration.value = player.duration.coerceAtLeast(0L)
                    }

                    if (state == Player.STATE_ENDED) {
                        _position.value = 0L
                    }
                }

                override fun onMediaItemTransition(
                    mediaItem: MediaItem?,
                    reason: Int
                ) {
                    updateCurrentSong()
                }

                override fun onPositionDiscontinuity(
                    oldPosition: Player.PositionInfo,
                    newPosition: Player.PositionInfo,
                    reason: Int
                ) {
                    updateCurrentSong()
                    updatePosition()
                }
            }
        )
    }

    fun setQueue(
        songs: List<Song>,
        startIndex: Int = 0,
        autoPlay: Boolean = true
    ) {
        if (songs.isEmpty()) {
            clearQueue()
            return
        }

        val safeIndex = startIndex.coerceIn(
            0,
            songs.lastIndex
        )

        _queue.value = songs
        currentIndex = safeIndex

        val mediaItems = songs.map { song ->
            MediaItem.Builder()
                .setMediaId(song.id.toString())
                .setUri(song.contentUri)
                .build()
        }

        player.setMediaItems(
            mediaItems,
            safeIndex,
            0L
        )

        player.prepare()

        if (autoPlay) {
            player.play()
        }

        updateCurrentSong()
        updatePosition()
    }

    fun play(song: Song) {
        val existingIndex = _queue.value.indexOfFirst {
            it.id == song.id
        }

        if (existingIndex >= 0) {
            currentIndex = existingIndex
            player.seekTo(existingIndex, 0L)
            player.play()
            updateCurrentSong()
            return
        }

        setQueue(
            songs = listOf(song),
            startIndex = 0,
            autoPlay = true
        )
    }

    fun play() {
        if (player.mediaItemCount == 0) {
            return
        }

        player.play()
    }

    fun pause() {
        player.pause()
    }

    fun resume() {
        if (player.mediaItemCount == 0) {
            return
        }

        player.play()
    }

    fun previous() {
        if (player.mediaItemCount == 0) {
            return
        }

        if (player.currentPosition > 3_000L) {
            player.seekTo(0L)
            updatePosition()
            return
        }

        player.seekToPrevious()
        updateCurrentSong()
        updatePosition()
    }

    fun next() {
        if (player.mediaItemCount == 0) {
            return
        }

        player.seekToNext()
        updateCurrentSong()
        updatePosition()
    }

    fun seekTo(position: Long) {
        if (player.mediaItemCount == 0) {
            return
        }

        val safePosition = position.coerceAtLeast(0L)

        player.seekTo(safePosition)
        updatePosition()
    }

    fun seekForward(
        milliseconds: Long = 10_000L
    ) {
        if (player.mediaItemCount == 0) {
            return
        }

        player.seekForward()
        updatePosition()
    }

    fun seekBackward(
        milliseconds: Long = 10_000L
    ) {
        if (player.mediaItemCount == 0) {
            return
        }

        player.seekBack()
        updatePosition()
    }

    fun setShuffleEnabled(enabled: Boolean) {
        _shuffleEnabled.value = enabled
        player.shuffleModeEnabled = enabled
    }

    fun toggleShuffle() {
        setShuffleEnabled(
            !_shuffleEnabled.value
        )
    }

    fun setRepeatMode(mode: Int) {
        if (
            mode != Player.REPEAT_MODE_OFF &&
            mode != Player.REPEAT_MODE_ONE &&
            mode != Player.REPEAT_MODE_ALL
        ) {
            return
        }

        _repeatMode.value = mode
        player.repeatMode = mode
    }

    fun toggleRepeat() {
        val nextMode = when (_repeatMode.value) {
            Player.REPEAT_MODE_OFF ->
                Player.REPEAT_MODE_ALL

            Player.REPEAT_MODE_ALL ->
                Player.REPEAT_MODE_ONE

            Player.REPEAT_MODE_ONE ->
                Player.REPEAT_MODE_OFF

            else ->
                Player.REPEAT_MODE_OFF
        }

        setRepeatMode(nextMode)
    }

    fun getCurrentSong(): Song? {
        return _currentSong.value
    }

    fun getCurrentPosition(): Long {
        return player.currentPosition
            .coerceAtLeast(0L)
    }

    fun getDuration(): Long {
        return player.duration
            .takeIf { it != C.TIME_UNSET }
            ?.coerceAtLeast(0L)
            ?: 0L
    }

    fun updatePosition() {
        _position.value = getCurrentPosition()
        _duration.value = getDuration()
    }

    fun clearQueue() {
        player.clearMediaItems()

        _queue.value = emptyList()
        _currentSong.value = null

        currentIndex = -1

        _position.value = 0L
        _duration.value = 0L
        _isPlaying.value = false
        _playbackState.value = Player.STATE_IDLE
    }

    fun release() {
        player.release()

        _queue.value = emptyList()
        _currentSong.value = null
        _isPlaying.value = false
        _position.value = 0L
        _duration.value = 0L
        _playbackState.value = Player.STATE_IDLE
    }

    private fun updateCurrentSong() {
        val index = player.currentMediaItemIndex

        if (
            index >= 0 &&
            index < _queue.value.size
        ) {
            currentIndex = index
            _currentSong.value = _queue.value[index]
        } else {
            _currentSong.value = null
        }
    }
}