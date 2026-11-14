package com.example.frem.data.media

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
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
import org.json.JSONArray
import org.json.JSONObject

class FremPlayer(context: Context) {
    private val appContext = context.applicationContext
    private val preferences = appContext.getSharedPreferences("frem_playback_state", Context.MODE_PRIVATE)
    private val player: ExoPlayer = ExoPlayer.Builder(appContext)
        .setAudioAttributes(
            AudioAttributes.Builder().setUsage(C.USAGE_MEDIA).setContentType(C.AUDIO_CONTENT_TYPE_MUSIC).build(),
            true
        )
        .build()
    private val handler = Handler(Looper.getMainLooper())
    private var released = false
    private var restoringQueue = false
    private var originalQueue: List<Song> = emptyList()
    private var currentIndex = -1

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

    private val progressRunnable = object : Runnable {
        override fun run() {
            if (released) return
            updatePosition()
            if (player.isPlaying) savePlaybackState()
            handler.postDelayed(this, PROGRESS_UPDATE_INTERVAL)
        }
    }

    init {
        player.repeatMode = Player.REPEAT_MODE_OFF
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
                updatePosition()
                savePlaybackState()
            }

            override fun onPlaybackStateChanged(state: Int) {
                _playbackState.value = state
                updatePosition()
                if (state == Player.STATE_ENDED) _position.value = 0L
                savePlaybackState()
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                updateCurrentSong()
                updateActiveQueue()
                updatePosition()
                savePlaybackState()
            }

            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo,
                newPosition: Player.PositionInfo,
                reason: Int
            ) {
                updateCurrentSong()
                updatePosition()
                savePlaybackState()
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
                _repeatMode.value = repeatMode
                savePlaybackState()
            }
        })
        handler.post(progressRunnable)
    }

    fun getPlayer(): Player = player
    fun isPlayingNow(): Boolean = player.isPlaying

    fun createQueue(
        name: String,
        sourceType: QueueSourceType,
        songs: List<Song>,
        startIndex: Int = 0,
        shuffle: Boolean = false,
        autoPlay: Boolean = true
    ) {
        checkMainThread()
        if (released) return
        val uniqueSongs = songs.distinctBy { it.id }
        if (uniqueSongs.isEmpty()) {
            clearQueue()
            return
        }

        val safeIndex = startIndex.coerceIn(0, uniqueSongs.lastIndex)
        val selectedSong = uniqueSongs[safeIndex]
        originalQueue = uniqueSongs
        val orderedSongs = if (shuffle) {
            listOf(selectedSong) + uniqueSongs.filterNot { it.id == selectedSong.id }.shuffled()
        } else {
            uniqueSongs
        }
        currentIndex = orderedSongs.indexOfFirst { it.id == selectedSong.id }.coerceAtLeast(0)
        _queue.value = orderedSongs
        _shuffleEnabled.value = shuffle
        _activeQueue.value = PlaybackQueue(
            id = buildQueueId(sourceType, uniqueSongs),
            name = name,
            sourceType = sourceType,
            songs = orderedSongs,
            currentIndex = currentIndex,
            shuffleEnabled = shuffle
        )

        player.shuffleModeEnabled = false
        player.setMediaItems(orderedSongs.map(::createMediaItem), currentIndex, 0L)
        player.repeatMode = _repeatMode.value
        player.prepare()
        if (autoPlay) player.play() else player.pause()
        updateCurrentSong()
        updatePosition()
        updateActiveQueue()
        savePlaybackState()
    }

    fun setQueue(songs: List<Song>, startIndex: Int = 0, autoPlay: Boolean = true) {
        createQueue(
            name = "Queue",
            sourceType = QueueSourceType.CUSTOM,
            songs = songs,
            startIndex = startIndex,
            autoPlay = autoPlay
        )
    }

    fun play(song: Song) {
        checkMainThread()
        if (released) return
        val index = _queue.value.indexOfFirst { it.id == song.id }
        if (index >= 0) {
            currentIndex = index
            player.seekTo(index, 0L)
            player.play()
            updateCurrentSong()
            updatePosition()
            updateActiveQueue()
            savePlaybackState()
        } else {
            createQueue(
                name = song.title,
                sourceType = QueueSourceType.CUSTOM,
                songs = listOf(song),
                autoPlay = true
            )
        }
    }

    fun playFromQueue(song: Song) {
        checkMainThread()
        if (released) return
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
        savePlaybackState()
    }

    fun hasQueue(): Boolean = _queue.value.isNotEmpty()
    fun getActiveQueue(): PlaybackQueue? = _activeQueue.value
    fun getCurrentQueueIndex(): Int = currentIndex

    fun play() {
        if (!released && player.mediaItemCount > 0) player.play()
    }

    fun pause() {
        if (!released) {
            player.pause()
            savePlaybackState()
        }
    }

    fun resume() = play()

    fun previous() {
        checkMainThread()
        if (released || player.mediaItemCount == 0) return
        if (player.currentPosition > PREVIOUS_RESTART_THRESHOLD) {
            player.seekTo(0L)
        } else if (currentIndex > 0) {
            player.seekTo(currentIndex - 1, 0L)
        } else if (_repeatMode.value == Player.REPEAT_MODE_ALL) {
            player.seekTo(_queue.value.lastIndex, 0L)
        } else {
            player.seekTo(0L)
        }
        updateCurrentSong()
        updatePosition()
        updateActiveQueue()
        savePlaybackState()
    }

    fun next() {
        checkMainThread()
        if (released || player.mediaItemCount == 0) return
        when {
            currentIndex < _queue.value.lastIndex -> player.seekTo(currentIndex + 1, 0L)
            _repeatMode.value == Player.REPEAT_MODE_ALL -> player.seekTo(0, 0L)
            else -> return
        }
        updateCurrentSong()
        updatePosition()
        updateActiveQueue()
        savePlaybackState()
        player.play()
    }

    fun seekTo(position: Long) {
        if (released || player.mediaItemCount == 0) return
        player.seekTo(position.coerceAtLeast(0L))
        updatePosition()
        savePlaybackState()
    }

    fun seekForward(milliseconds: Long = 10_000L) {
        if (released || player.mediaItemCount == 0) return
        val target = player.currentPosition + milliseconds.coerceAtLeast(0L)
        player.seekTo(target.coerceAtMost(getDuration()))
        updatePosition()
        savePlaybackState()
    }

    fun seekBackward(milliseconds: Long = 10_000L) {
        if (released || player.mediaItemCount == 0) return
        val target = player.currentPosition - milliseconds.coerceAtLeast(0L)
        player.seekTo(target.coerceAtLeast(0L))
        updatePosition()
        savePlaybackState()
    }

    fun setShuffleEnabled(enabled: Boolean) {
        checkMainThread()
        if (released || _shuffleEnabled.value == enabled) return
        val currentSong = _currentSong.value
        if (_queue.value.isEmpty()) {
            _shuffleEnabled.value = enabled
            savePlaybackState()
            return
        }

        val songs = if (originalQueue.isNotEmpty()) originalQueue else _queue.value
        val selectedSong = currentSong ?: _queue.value.getOrNull(currentIndex) ?: return
        val reorderedSongs = if (enabled) {
            listOf(selectedSong) + songs.filterNot { it.id == selectedSong.id }.shuffled()
        } else {
            songs
        }

        originalQueue = songs
        _queue.value = reorderedSongs
        _shuffleEnabled.value = enabled
        currentIndex = reorderedSongs.indexOfFirst { it.id == selectedSong.id }.coerceAtLeast(0)
        rebuildPlayerQueue()
        updateCurrentSong()
        updatePosition()
        updateActiveQueue()
        savePlaybackState()
    }

    fun toggleShuffle() = setShuffleEnabled(!_shuffleEnabled.value)

    fun setRepeatMode(mode: Int) {
        if (released) return
        if (mode != Player.REPEAT_MODE_OFF && mode != Player.REPEAT_MODE_ONE && mode != Player.REPEAT_MODE_ALL) return
        _repeatMode.value = mode
        player.repeatMode = mode
        savePlaybackState()
    }

    fun toggleRepeat() {
        val nextMode = when (_repeatMode.value) {
            Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
            Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
            else -> Player.REPEAT_MODE_OFF
        }
        setRepeatMode(nextMode)
    }

    fun getCurrentSong(): Song? = _currentSong.value
    fun getCurrentPosition(): Long = if (released) 0L else player.currentPosition.coerceAtLeast(0L)

    fun getDuration(): Long {
        if (released) return 0L
        return player.duration.takeIf { it != C.TIME_UNSET }?.coerceAtLeast(0L) ?: 0L
    }

    fun updatePosition() {
        if (released) return
        _position.value = getCurrentPosition()
        _duration.value = getDuration()
    }

    suspend fun restoreSavedQueue(loadSongs: suspend (List<Long>) -> List<Song>) {
        checkMainThread()
        if (released || _queue.value.isNotEmpty()) return
        val jsonText = preferences.getString(KEY_QUEUE, null) ?: return

        try {
            val saved = JSONObject(jsonText)
            val idsJson = saved.optJSONArray("songIds") ?: return
            val ids = buildList {
                for (i in 0 until idsJson.length()) add(idsJson.getLong(i))
            }
            if (ids.isEmpty()) return

            val loadedSongs = loadSongs(ids)
            val songsById = loadedSongs.associateBy { it.id }
            val restoredSongs = ids.mapNotNull { songsById[it] }
            if (restoredSongs.isEmpty()) return

            val sourceType = runCatching {
                QueueSourceType.valueOf(saved.optString("sourceType", QueueSourceType.CUSTOM.name))
            }.getOrDefault(QueueSourceType.CUSTOM)
            val shuffle = saved.optBoolean("shuffleEnabled", false)
            val repeat = saved.optInt("repeatMode", Player.REPEAT_MODE_OFF)
            _repeatMode.value = repeat.takeIf {
                it == Player.REPEAT_MODE_OFF || it == Player.REPEAT_MODE_ONE || it == Player.REPEAT_MODE_ALL
            } ?: Player.REPEAT_MODE_OFF

            val savedCurrentSongId = if (saved.has("currentSongId")) saved.optLong("currentSongId") else null
            val savedIndex = saved.optInt("currentIndex", 0).coerceIn(0, restoredSongs.lastIndex)
            val selectedIndex = savedCurrentSongId
                ?.let { id -> restoredSongs.indexOfFirst { it.id == id } }
                ?.takeIf { it >= 0 } ?: savedIndex
            val position = saved.optLong("position", 0L).coerceAtLeast(0L)

            restoringQueue = true
            val name = saved.optString("queueName", "Queue")
            createQueue(
                name = name,
                sourceType = sourceType,
                songs = restoredSongs,
                startIndex = selectedIndex,
                shuffle = shuffle,
                autoPlay = false
            )
            player.seekTo(selectedIndex, position)
            player.pause()
            updateCurrentSong()
            updatePosition()
            updateActiveQueue()
            restoringQueue = false
            savePlaybackState()
        } catch (_: Exception) {
            restoringQueue = false
        }
    }

    private fun savePlaybackState() {
        if (released || restoringQueue || _queue.value.isEmpty()) return
        val queue = _activeQueue.value ?: return
        val ids = JSONArray()
        _queue.value.forEach { song -> ids.put(song.id) }
        val currentSongId = _currentSong.value?.id
        val json = JSONObject().apply {
            put("queueName", queue.name)
            put("sourceType", queue.sourceType.name)
            put("songIds", ids)
            put("currentIndex", currentIndex.coerceAtLeast(0))
            put("currentSongId", currentSongId)
            put("position", getCurrentPosition())
            put("shuffleEnabled", _shuffleEnabled.value)
            put("repeatMode", _repeatMode.value)
        }
        preferences.edit().putString(KEY_QUEUE, json.toString()).apply()
    }

    fun clearQueue() {
        checkMainThread()
        if (released) {
            preferences.edit().remove(KEY_QUEUE).apply()
            return
        }
        player.stop()
        player.clearMediaItems()
        _queue.value = emptyList()
        _activeQueue.value = null
        _currentSong.value = null
        originalQueue = emptyList()
        currentIndex = -1
        _position.value = 0L
        _duration.value = 0L
        _isPlaying.value = false
        _playbackState.value = Player.STATE_IDLE
        _shuffleEnabled.value = false
        preferences.edit().remove(KEY_QUEUE).apply()
    }

    fun release() {
        if (released) return
        savePlaybackState()
        handler.removeCallbacks(progressRunnable)
        player.release()
        released = true
        _isPlaying.value = false
        _playbackState.value = Player.STATE_IDLE
    }

    private fun createMediaItem(song: Song): MediaItem =
        MediaItem.Builder().setMediaId(song.id.toString()).setUri(Uri.parse(song.contentUri)).build()

    private fun updateCurrentSong() {
        val index = player.currentMediaItemIndex
        if (index in _queue.value.indices) {
            currentIndex = index
            _currentSong.value = _queue.value[index]
        } else {
            _currentSong.value = null
        }
    }

    private fun updateActiveQueue() {
        val existing = _activeQueue.value ?: return
        _activeQueue.value = existing.copy(
            songs = _queue.value,
            currentIndex = currentIndex,
            shuffleEnabled = _shuffleEnabled.value
        )
    }

    private fun rebuildPlayerQueue() {
        val songs = _queue.value
        if (songs.isEmpty()) return
        val position = player.currentPosition.coerceAtLeast(0L)
        val wasPlaying = player.isPlaying
        player.setMediaItems(songs.map(::createMediaItem), currentIndex.coerceIn(0, songs.lastIndex), position)
        player.repeatMode = _repeatMode.value
        player.prepare()
        if (wasPlaying) player.play() else player.pause()
    }

    private fun buildQueueId(sourceType: QueueSourceType, songs: List<Song>): String {
        val songIds = songs.joinToString("-") { it.id.toString() }
        return "${sourceType.name.lowercase()}_$songIds"
    }

    private fun checkMainThread() {
        check(Looper.myLooper() == Looper.getMainLooper()) {
            "FremPlayer must be accessed from the main thread."
        }
    }

    companion object {
        private const val KEY_QUEUE = "saved_queue"
        private const val PROGRESS_UPDATE_INTERVAL = 1_000L
        private const val PREVIOUS_RESTART_THRESHOLD = 3_000L
    }
}