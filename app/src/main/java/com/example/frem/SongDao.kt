package com.example.frem

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {

    /**
     * Insert a song.
     *
     * If the song already exists, replace the existing record.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSong(song: SongEntity)

    /**
     * Insert multiple songs.
     *
     * Existing songs with the same ID will be replaced.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongs(songs: List<SongEntity>)

    /**
     * Update an existing song.
     */
    @Update
    suspend fun updateSong(song: SongEntity)

    /**
     * Delete a single song.
     */
    @Delete
    suspend fun deleteSong(song: SongEntity)

    /**
     * Delete a song using its ID.
     */
    @Query("DELETE FROM songs WHERE id = :songId")
    suspend fun deleteSongById(songId: Long)

    /**
     * Delete all songs.
     */
    @Query("DELETE FROM songs")
    suspend fun deleteAllSongs()

    /**
     * Get all songs ordered alphabetically by title.
     *
     * Flow automatically emits a new list whenever the
     * songs table changes.
     */
    @Query("SELECT * FROM songs ORDER BY title COLLATE NOCASE ASC")
    fun getAllSongs(): Flow<List<SongEntity>>

    /**
     * Get a single song by its ID.
     */
    @Query("SELECT * FROM songs WHERE id = :songId LIMIT 1")
    suspend fun getSongById(songId: Long): SongEntity?

    /**
     * Search songs by title, artist, album, or album artist.
     */
    @Query(
        """
        SELECT * FROM songs
        WHERE title LIKE '%' || :query || '%'
           OR artist LIKE '%' || :query || '%'
           OR album LIKE '%' || :query || '%'
           OR albumArtist LIKE '%' || :query || '%'
        ORDER BY title COLLATE NOCASE ASC
        """
    )
    fun searchSongs(query: String): Flow<List<SongEntity>>

    /**
     * Get all songs belonging to an album.
     */
    @Query(
        """
        SELECT * FROM songs
        WHERE album = :album
        ORDER BY trackNumber ASC, title COLLATE NOCASE ASC
        """
    )
    fun getSongsByAlbum(album: String): Flow<List<SongEntity>>

    /**
     * Get all songs by an artist.
     */
    @Query(
        """
        SELECT * FROM songs
        WHERE artist = :artist
        ORDER BY album COLLATE NOCASE ASC,
                 trackNumber ASC,
                 title COLLATE NOCASE ASC
        """
    )
    fun getSongsByArtist(artist: String): Flow<List<SongEntity>>

    /**
     * Get all favorite songs.
     */
    @Query(
        """
        SELECT * FROM songs
        WHERE isFavorite = 1
        ORDER BY title COLLATE NOCASE ASC
        """
    )
    fun getFavoriteSongs(): Flow<List<SongEntity>>

    /**
     * Update favorite status.
     */
    @Query(
        """
        UPDATE songs
        SET isFavorite = :isFavorite
        WHERE id = :songId
        """
    )
    suspend fun updateFavorite(
        songId: Long,
        isFavorite: Boolean
    )

    /**
     * Update play count and last played timestamp.
     */
    @Query(
        """
        UPDATE songs
        SET playCount = playCount + 1,
            lastPlayed = :playedAt
        WHERE id = :songId
        """
    )
    suspend fun recordPlay(
        songId: Long,
        playedAt: Long = System.currentTimeMillis()
    )

    /**
     * Get recently played songs.
     */
    @Query(
        """
        SELECT * FROM songs
        WHERE lastPlayed IS NOT NULL
        ORDER BY lastPlayed DESC
        """
    )
    fun getRecentlyPlayed(): Flow<List<SongEntity>>

    /**
     * Get most played songs.
     */
    @Query(
        """
        SELECT * FROM songs
        ORDER BY playCount DESC, title COLLATE NOCASE ASC
        """
    )
    fun getMostPlayed(): Flow<List<SongEntity>>

    /**
     * Get the number of songs currently stored.
     */
    @Query("SELECT COUNT(*) FROM songs")
    fun getSongCount(): Flow<Int>
}