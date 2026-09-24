package com.example.frem.data.media

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import com.example.frem.SongDao
import com.example.frem.SongEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MusicScanner(
    private val context: Context,
    private val songDao: SongDao
) {

    /**
     * Scans MediaStore for all music files available on the device
     * and synchronizes them with the local Room database.
     *
     * Returns the number of songs discovered.
     */
    suspend fun scan(): Int = withContext(Dispatchers.IO) {

        if (!hasAudioPermission()) {
            return@withContext 0
        }

        val songs = mutableListOf<SongEntity>()

        val collection = MediaStore.Audio.Media.getContentUri(
            MediaStore.VOLUME_EXTERNAL
        )

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ALBUM_ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.YEAR,
            MediaStore.Audio.Media.MIME_TYPE,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATE_MODIFIED,
            MediaStore.Audio.Media.DATA
        )

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"

        context.contentResolver.query(
            collection,
            projection,
            selection,
            null,
            "${MediaStore.Audio.Media.TITLE} COLLATE NOCASE ASC"
        )?.use { cursor ->

            val idColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)

            val titleColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)

            val artistColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)

            val albumColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)

            val albumIdColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

            val albumArtistColumn =
                cursor.getColumnIndex(
                    MediaStore.Audio.Media.ALBUM_ARTIST
                )

            val durationColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

            val trackColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK)

            val yearColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)

            val mimeTypeColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE)

            val sizeColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)

            val dateAddedColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)

            val dateModifiedColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED)

            val dataColumn =
                cursor.getColumnIndex(MediaStore.Audio.Media.DATA)

            while (cursor.moveToNext()) {

                val id = cursor.getLong(idColumn)

                val title = cursor.getString(titleColumn)
                    ?.takeIf { it.isNotBlank() }
                    ?: "Unknown title"

                val artist = cursor.getString(artistColumn)
                    ?.takeIf { it.isNotBlank() }
                    ?: "Unknown artist"

                val album = cursor.getString(albumColumn)
                    ?.takeIf { it.isNotBlank() }
                    ?: "Unknown album"

                val albumArtist = if (albumArtistColumn >= 0) {
                    cursor.getString(albumArtistColumn)
                        ?.takeIf { it.isNotBlank() }
                        ?: artist
                } else {
                    artist
                }

                val albumId = cursor.getLong(albumIdColumn)

                val duration = cursor.getLong(durationColumn)

                val trackNumber = cursor.getInt(trackColumn) % 1000

                val year = cursor.getInt(yearColumn)

                val mimeType = cursor.getString(mimeTypeColumn)
                    ?: "audio/*"

                val size = cursor.getLong(sizeColumn)

                val dateAdded = cursor.getLong(dateAddedColumn)

                val dateModified = cursor.getLong(dateModifiedColumn)

                val contentUri = ContentUris.withAppendedId(
                    collection,
                    id
                )

                val path = if (dataColumn >= 0) {
                    cursor.getString(dataColumn) ?: ""
                } else {
                    ""
                }
                val albumArtworkUri = getAlbumArtworkUri(albumId)

                val existingSong = songDao.getSongById(id)

                val song = SongEntity(
                    id = id,
                    title = title,
                    artist = artist,
                    album = album,
                    albumArtist = albumArtist,
                    duration = duration,
                    trackNumber = trackNumber,
                    year = year,
                    genre = "",
                    contentUri = contentUri.toString(),
                    path = path,
                    mimeType = mimeType,
                    size = size,
                    dateAdded = dateAdded,
                    dateModified = dateModified,
                    albumId = albumId,
                    albumArtworkUri = albumArtworkUri?.toString(),

                    isFavorite = existingSong?.isFavorite ?: false,
                    playCount = existingSong?.playCount ?: 0,
                    lastPlayed = existingSong?.lastPlayed,
                    dateAddedToLibrary =
                        existingSong?.dateAddedToLibrary
                            ?: System.currentTimeMillis()
                )

                songs.add(song)
            }
        }

        if (songs.isNotEmpty()) {
            songDao.insertSongs(songs)
        }

        songs.size
    }

    /**
     * Returns the URI used by Android for album artwork.
     *
     * Example:
     * content://media/external/audio/albumart/123
     */
    private fun getAlbumArtworkUri(
        albumId: Long
    ): Uri {

        return ContentUris.withAppendedId(
            Uri.parse(
                "content://media/external/audio/albumart"
            ),
            albumId
        )
    }

    /**
     * Check whether the application has permission to read
     * audio files from MediaStore.
     */
    private fun hasAudioPermission(): Boolean {

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_MEDIA_AUDIO
            ) == PackageManager.PERMISSION_GRANTED

        } else {

            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
}