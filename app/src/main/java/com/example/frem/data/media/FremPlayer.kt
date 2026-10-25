package com.example.frem.data.media

import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.frem.domain.model.PlaybackQueue
import com.example.frem.domain.model.QueueSourceType
import com.example.frem.domain.model.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FremPlayer(context: Context) {
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

    private val _activeQueue = MutableStateFlow<PlaybackQueue?>(null)
    val activeQueue: StateFlow<PlaybackQueue?> = _activeQueue.asStateFlow()

    private val _shuffleEnabled = MutableStateFlow(false)
    val shuffleEnabled: StateFlow<Boolean> = _shuffleEnabled.asStateFlow()

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

                    if (state == Player.STATE_READY)
                        _duration.value = player.duration.coerceAtLeast(0L)

                    if (state == Player.STATE_ENDED)
                        _position.value = 0L
                }

                override fun onMediaItemTransition(
                    mediaItem: MediaItem?,
                    reason: Int
                ) {
                    updateCurrentSong()
                    updateActiveQueue()
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

    fun getPlayer(): Player {
        return player
    }

    fun isPlayingNow(): Boolean {
        return player.isPlaying
    }

    // QUEUE

    fun createQueue(
        name: String,
        sourceType: QueueSourceType,
        songs: List<Song>,
        startIndex: Int = 0,
        shuffle: Boolean = false,
        autoPlay: Boolean = true
    ) {
        if (songs.isEmpty()) {
            clearQueue()
            return
        }

        val originalSongs = songs.distinctBy { it.id }

        if (originalSongs.isEmpty()) {
            clearQueue()
            return
        }

        val safeIndex = startIndex.coerceIn(0, originalSongs.lastIndex)

        val orderedSongs: List<Song>

        if (shuffle) {
            val selectedSong = originalSongs[safeIndex]
            val remainingSongs = originalSongs
                .filterIndexed { index, _ -> index != safeIndex }
                .shuffled()

            orderedSongs = listOf(selectedSong) + remainingSongs
        } else {
            orderedSongs = originalSongs
        }

        val queueId = buildQueueId(
            sourceType = sourceType,
            songs = originalSongs
        )

        val playbackQueue = PlaybackQueue(
            id = queueId,
            name = name,
            sourceType = sourceType,
            songs = orderedSongs,
            currentIndex = if (shuffle) 0 else safeIndex,
            shuffleEnabled = shuffle
        )

        _activeQueue.value = playbackQueue
        _queue.value = orderedSongs
        _shuffleEnabled.value = shuffle
        currentIndex = if (shuffle) 0 else safeIndex

        val mediaItems = orderedSongs.map { song ->
            MediaItem.Builder()
                .setMediaId(song.id.toString())
                .setUri(song.contentUri)
                .build()
        }

        player.setMediaItems(mediaItems, currentIndex, 0L)
        player.prepare()

        if (autoPlay)
            player.play()

        updateCurrentSong()
        updatePosition()
        updateActiveQueue()
    }

    fun setQueue(
        songs: List<Song>,
        startIndex: Int = 0,
        autoPlay: Boolean = true
    ) {
        createQueue(
            name = "Queue",
            sourceType = QueueSourceType.CUSTOM,
            songs = songs,
            startIndex = startIndex,
            shuffle = false,
            autoPlay = autoPlay
        )
    }

    fun play(song: Song) {
        val existingIndex = _queue.value.indexOfFirst { it.id == song.id }

        if (existingIndex >= 0) {
            currentIndex = existingIndex
            player.seekTo(existingIndex, 0L)
            player.play()
            updateCurrentSong()
            updatePosition()
            updateActiveQueue()
            return
        }

        createQueue(
            name = song.title,
            sourceType = QueueSourceType.CUSTOM,
            songs = listOf(song),
            startIndex = 0,
            shuffle = false,
            autoPlay = true
        )
    }

    fun playFromQueue(song: Song) {
        val index = _queue.value.indexOfFirst { it.id == song.id }

        if (index < 0) {
            play(song)
            return
        }

        currentIndex = index
        player.seekTo(index, 0L)
        player.play()
        updateCurrentSong()
        updatePosition()
        updateActiveQueue()
    }

    fun hasQueue(): Boolean {
        return _queue.value.isNotEmpty()
    }

    fun getActiveQueue(): PlaybackQueue? {
        return _activeQueue.value
    }

    fun getCurrentQueueIndex(): Int {
        return currentIndex
    }

    // PLAYBACK

    fun play() {
        if (player.mediaItemCount == 0)
            return

        player.play()
    }

    fun pause() {
        player.pause()
    }

    fun resume() {
        if (player.mediaItemCount == 0)
            return

        player.play()
    }

    fun previous() {
        if (player.mediaItemCount == 0)
            return

        if (player.currentPosition > 3_000L) {
            player.seekTo(0L)
            updatePosition()
            return
        }

        if (currentIndex > 0) {
            currentIndex--

            player.seekTo(currentIndex, 0L)
            updateCurrentSong()
            updatePosition()
            updateActiveQueue()
        } else if (_repeatMode.value == Player.REPEAT_MODE_ALL) {
            currentIndex = _queue.value.lastIndex

            player.seekTo(currentIndex, 0L)
            updateCurrentSong()
            updatePosition()
            updateActiveQueue()
        }
    }

    fun next() {
        if (player.mediaItemCount == 0)
            return

        if (currentIndex < _queue.value.lastIndex) {
            currentIndex++

            player.seekTo(currentIndex, 0L)
            updateCurrentSong()
            updatePosition()
            updateActiveQueue()

            if (!player.isPlaying)
                player.play()

            return
        }

        if (_repeatMode.value == Player.REPEAT_MODE_ALL) {
            currentIndex = 0

            player.seekTo(currentIndex, 0L)
            updateCurrentSong()
            updatePosition()
            updateActiveQueue()
            player.play()
        }
    }

    // SEEK

    fun seekTo(position: Long) {
        if (player.mediaItemCount == 0)
            return

        player.seekTo(position.coerceAtLeast(0L))
        updatePosition()
    }

    fun seekForward(milliseconds: Long = 10_000L) {
        if (player.mediaItemCount == 0)
            return

        val target = player.currentPosition + milliseconds.coerceAtLeast(0L)

        player.seekTo(target.coerceAtMost(getDuration()))
        updatePosition()
    }

    fun seekBackward(milliseconds: Long = 10_000L) {
        if (player.mediaItemCount == 0)
            return

        val target = player.currentPosition - milliseconds.coerceAtLeast(0L)

        player.seekTo(target.coerceAtLeast(0L))
        updatePosition()
    }

    // SHUFFLE

    fun setShuffleEnabled(enabled: Boolean) {
        if (_queue.value.isEmpty()) {
            _shuffleEnabled.value = enabled
            return
        }

        if (_shuffleEnabled.value == enabled)
            return

        val currentSong = _currentSong.value

        if (currentSong == null) {
            _shuffleEnabled.value = enabled
            return
        }

        val songs = _queue.value

        val reorderedSongs: List<Song>

        if (enabled) {
            val remaining = songs
                .filter { it.id != currentSong.id }
                .shuffled()

            reorderedSongs = listOf(currentSong) + remaining
            currentIndex = 0
        } else {
            reorderedSongs = songs

            currentIndex = reorderedSongs
                .indexOfFirst { it.id == currentSong.id }
                .coerceAtLeast(0)
        }

        _queue.value = reorderedSongs
        _shuffleEnabled.value = enabled

        rebuildPlayerQueue()
        updateCurrentSong()
        updatePosition()
        updateActiveQueue()
    }

    fun toggleShuffle() {
        setShuffleEnabled(!_shuffleEnabled.value)
    }

    // REPEAT

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
            Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
            Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
            Player.REPEAT_MODE_ONE -> Player.REPEAT_MODE_OFF
            else -> Player.REPEAT_MODE_OFF
        }

        setRepeatMode(nextMode)
    }

    // STATE

    fun getCurrentSong(): Song? {
        return _currentSong.value
    }

    fun getCurrentPosition(): Long {
        return player.currentPosition.coerceAtLeast(0L)
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

    // CLEAR / RELEASE

    fun clearQueue() {
        player.stop()
        player.clearMediaItems()

        _queue.value = emptyList()
        _activeQueue.value = null
        _currentSong.value = null

        currentIndex = -1

        _position.value = 0L
        _duration.value = 0L
        _isPlaying.value = false
        _playbackState.value = Player.STATE_IDLE
        _shuffleEnabled.value = false
    }

    fun release() {
        player.release()

        _queue.value = emptyList()
        _activeQueue.value = null
        _currentSong.value = null
        _isPlaying.value = false
        _position.value = 0L
        _duration.value = 0L
        _playbackState.value = Player.STATE_IDLE
    }

    // INTERNAL

    private fun updateCurrentSong() {
        val index = player.currentMediaItemIndex

        if (index >= 0 && index < _queue.value.size) {
            currentIndex = index
            _currentSong.value = _queue.value[index]
        } else {
            _currentSong.value = null
        }
    }

    private fun updateActiveQueue() {
        val existingQueue = _activeQueue.value ?: return

        _activeQueue.value = existingQueue.copy(
            songs = _queue.value,
            currentIndex = currentIndex,
            shuffleEnabled = _shuffleEnabled.value
        )
    }

    private fun rebuildPlayerQueue() {
        val mediaItems = _queue.value.map { song ->
            MediaItem.Builder()
                .setMediaId(song.id.toString())
                .setUri(song.contentUri)
                .build()
        }

        if (mediaItems.isEmpty())
            return

        player.setMediaItems(
            mediaItems,
            currentIndex.coerceIn(0, mediaItems.lastIndex),
            player.currentPosition
        )

        player.prepare()
    }

    private fun buildQueueId(
        sourceType: QueueSourceType,
        songs: List<Song>
    ): String {
        val songIds = songs.joinToString("-") { it.id.toString() }
        return "${sourceType.name.lowercase()}_$songIds"
    }
}