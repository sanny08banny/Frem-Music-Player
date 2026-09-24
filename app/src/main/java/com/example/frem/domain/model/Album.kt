package com.example.frem.domain.model

data class Album(
    val id: Long,

    val title: String,

    val artist: String,

    val albumArtist: String,

    val year: Int,

    val artworkUri: String? = null,

    val songs: List<Song> = emptyList()
)