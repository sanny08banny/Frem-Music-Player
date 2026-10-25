package com.example.frem.domain.model

data class PlaybackQueue(
    val id: String,
    val name: String,
    val sourceType: QueueSourceType,
    val songs: List<Song>,
    val currentIndex: Int = 0,
    val shuffleEnabled: Boolean = false
) {
    val currentSong: Song?
        get() = songs.getOrNull(currentIndex)

    val isEmpty: Boolean
        get() = songs.isEmpty()
}