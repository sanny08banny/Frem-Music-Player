package com.example.frem.domain.model

data class Song(
    val id: Long,

    val title: String,

    val artist: String,

    val album: String,

    val albumArtist: String,

    val duration: Long,

    val trackNumber: Int,

    val year: Int,

    val genre: String,

    val contentUri: String,

    val path: String,

    val mimeType: String,

    val size: Long,

    val dateAdded: Long,

    val dateModified: Long,

    val albumId: Long,
    val albumArtworkUri: String? = null,

    val isFavorite: Boolean = false,

    val playCount: Int = 0,

    val lastPlayed: Long? = null,

    val dateAddedToLibrary: Long
)