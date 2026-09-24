package com.example.frem.domain.model

data class Artist(
    val id: Long,

    val name: String,

    val albumCount: Int = 0,

    val songCount: Int = 0,

    val albums: List<Album> = emptyList(),

    val songs: List<Song> = emptyList()
)