package com.example.frem.domain.model

data class Playlist(
    val id: Long,

    val name: String,

    val description: String = "",

    val dateCreated: Long,

    val dateModified: Long,

    val songs: List<Song> = emptyList()
)