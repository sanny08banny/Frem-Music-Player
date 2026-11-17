
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
     * Scan MediaStore and synchronize music with Room.
     */
    suspend fun scanMusic(): Int {
        return musicScanner.scan()
    }

    /**
     * Refresh the music library.
     */
    suspend fun refreshSongs(): Int {
        return musicScanner.scan()
    }

    fun getAllSongs(): Flow<List<Song>> {
        return songDao
            .getAllSongs()
            .map { songs ->
                songs.map { it.toDomainModel() }
            }
    }

    suspend fun getSongById(songId: Long): Song? {
        return songDao
            .getSongById(songId)
            ?.toDomainModel()
    }

    fun searchSongs(query: String): Flow<List<Song>> {
        return songDao
            .searchSongs(query)
            .map { songs ->
                songs.map { it.toDomainModel() }
            }
    }

    fun getSongsByAlbum(album: String): Flow<List<Song>> {
        return songDao
            .getSongsByAlbum(album)
            .map { songs ->
                songs.map { it.toDomainModel() }
            }
    }

    fun getSongsByArtist(artist: String): Flow<List<Song>> {
        return songDao
            .getSongsByArtist(artist)
            .map { songs ->
                songs.map { it.toDomainModel() }
            }
    }

    fun getFavoriteSongs(): Flow<List<Song>> {
        return songDao
            .getFavoriteSongs()
            .map { songs ->
                songs.map { it.toDomainModel() }
            }
    }

    suspend fun setFavorite(
        songId: Long,
        isFavorite: Boolean
    ) {
        songDao.updateFavorite(
            songId = songId,
            isFavorite = isFavorite
        )
    }

    suspend fun toggleFavorite(song: Song) {
        songDao.updateFavorite(
            songId = song.id,
            isFavorite = !song.isFavorite
        )
    }

    suspend fun recordPlay(songId: Long) {
        songDao.recordPlay(songId)
    }

    fun getRecentlyPlayed(): Flow<List<Song>> {
        return songDao
            .getRecentlyPlayed()
            .map { songs ->
                songs.map { it.toDomainModel() }
            }
    }

    fun getMostPlayed(): Flow<List<Song>> {
        return songDao
            .getMostPlayed()
            .map { songs ->
                songs.map { it.toDomainModel() }
            }
    }

    fun getSongCount(): Flow<Int> {
        return songDao.getSongCount()
    }

    suspend fun deleteSong(songId: Long) {
        songDao.deleteSongById(songId)
    }

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
