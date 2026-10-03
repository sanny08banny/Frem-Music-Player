package com.example.frem.data.repository

import com.example.frem.models.SongDao
import com.example.frem.models.SongEntity
import com.example.frem.data.media.MusicScanner
import com.example.frem.domain.model.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MusicRepository(
    private val songDao: SongDao,
    private val musicScanner: MusicScanner
) {

    /**
     * Scan the device's MediaStore and synchronize
     * the discovered music with Room.
     *
     * Returns the number of songs discovered.
     */
    suspend fun scanMusic(): Int {
        return musicScanner.scan()
    }

    /**
     * Get every song in the local music library.
     */
    fun getAllSongs(): Flow<List<Song>> {
        return songDao
            .getAllSongs()
            .map { songs ->
                songs.map { it.toDomainModel() }
            }
    }

    /**
     * Get a single song.
     */
    suspend fun getSongById(songId: Long): Song? {
        return songDao
            .getSongById(songId)
            ?.toDomainModel()
    }

    /**
     * Search songs by title, artist, album, or album artist.
     */
    fun searchSongs(query: String): Flow<List<Song>> {
        return songDao
            .searchSongs(query)
            .map { songs ->
                songs.map { it.toDomainModel() }
            }
    }

    /**
     * Get all songs belonging to an album.
     */
    fun getSongsByAlbum(album: String): Flow<List<Song>> {
        return songDao
            .getSongsByAlbum(album)
            .map { songs ->
                songs.map { it.toDomainModel() }
            }
    }

    /**
     * Get all songs belonging to an artist.
     */
    fun getSongsByArtist(artist: String): Flow<List<Song>> {
        return songDao
            .getSongsByArtist(artist)
            .map { songs ->
                songs.map { it.toDomainModel() }
            }
    }

    /**
     * Get favorite songs.
     */
    fun getFavoriteSongs(): Flow<List<Song>> {
        return songDao
            .getFavoriteSongs()
            .map { songs ->
                songs.map { it.toDomainModel() }
            }
    }

    /**
     * Mark or unmark a song as favorite.
     */
    suspend fun setFavorite(
        songId: Long,
        isFavorite: Boolean
    ) {
        songDao.updateFavorite(
            songId = songId,
            isFavorite = isFavorite
        )
    }

    /**
     * Toggle the favorite state of a song.
     */
    suspend fun toggleFavorite(song: Song) {
        songDao.updateFavorite(
            songId = song.id,
            isFavorite = !song.isFavorite
        )
    }

    /**
     * Record that a song was played.
     */
    suspend fun recordPlay(songId: Long) {
        songDao.recordPlay(songId)
    }

    /**
     * Get recently played songs.
     */
    fun getRecentlyPlayed(): Flow<List<Song>> {
        return songDao
            .getRecentlyPlayed()
            .map { songs ->
                songs.map { it.toDomainModel() }
            }
    }

    /**
     * Get most played songs.
     */
    fun getMostPlayed(): Flow<List<Song>> {
        return songDao
            .getMostPlayed()
            .map { songs ->
                songs.map { it.toDomainModel() }
            }
    }

    /**
     * Get total number of songs.
     */
    fun getSongCount(): Flow<Int> {
        return songDao.getSongCount()
    }

    /**
     * Delete a song from the local database.
     */
    suspend fun deleteSong(songId: Long) {
        songDao.deleteSongById(songId)
    }

    /**
     * Convert the Room database model into
     * the app-level domain model.
     */
    private fun SongEntity.toDomainModel(): Song {

        return Song(
            id = id,

            title = title,

            artist = artist,

            album = album,

            albumArtist = albumArtist,

            duration = duration,

            trackNumber = trackNumber,

            year = year,

            genre = genre,

            contentUri = contentUri,

            path = path,

            mimeType = mimeType,

            size = size,

            dateAdded = dateAdded,

            dateModified = dateModified,

            albumId = albumId,

            albumArtworkUri = albumArtworkUri,

            isFavorite = isFavorite,

            playCount = playCount,

            lastPlayed = lastPlayed,

            dateAddedToLibrary = dateAddedToLibrary
        )
    }
}