package com.example.frem.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "songs",
    indices = [
        Index(value = ["title"]),
        Index(value = ["artist"]),
        Index(value = ["album"]),
        Index(value = ["albumArtist"]),
        Index(value = ["contentUri"], unique = true)
    ]
)
data class SongEntity(

    @PrimaryKey
    val id: Long,

    // Basic song information
    val title: String,
    val artist: String,
    val album: String,
    val albumArtist: String,
    val albumArtworkUri: String? = null,

    // Audio information
    val duration: Long,
    val trackNumber: Int,
    val year: Int,
    val genre: String,

    // MediaStore information
    val contentUri: String,
    val path: String,
    val mimeType: String,
    val size: Long,
    val dateAdded: Long,
    val dateModified: Long,

    // Artwork
    val albumId: Long,

    // Frem library state
    val isFavorite: Boolean = false,
    val playCount: Int = 0,
    val lastPlayed: Long? = null,
    val dateAddedToLibrary: Long = System.currentTimeMillis()
)