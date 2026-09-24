package com.example.frem

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    // ---------------------------------------------------------
    // PLAYLISTS
    // ---------------------------------------------------------

    /**
     * Create a new playlist.
     *
     * Returns the generated playlist ID.
     */
    @Insert
    suspend fun createPlaylist(
        playlist: PlaylistEntity
    ): Long

    /**
     * Get all playlists.
     */
    @Query(
        """
        SELECT * FROM playlists
        ORDER BY name COLLATE NOCASE ASC
        """
    )
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    /**
     * Get a playlist by ID.
     */
    @Query(
        """
        SELECT * FROM playlists
        WHERE id = :playlistId
        LIMIT 1
        """
    )
    suspend fun getPlaylistById(
        playlistId: Long
    ): PlaylistEntity?

    /**
     * Rename a playlist.
     */
    @Query(
        """
        UPDATE playlists
        SET name = :name,
            dateModified = :dateModified
        WHERE id = :playlistId
        """
    )
    suspend fun renamePlaylist(
        playlistId: Long,
        name: String,
        dateModified: Long = System.currentTimeMillis()
    )

    /**
     * Update an entire playlist.
     */
    @Update
    suspend fun updatePlaylist(
        playlist: PlaylistEntity
    )

    /**
     * Delete a playlist.
     *
     * Because PlaylistSongEntity uses CASCADE,
     * its songs relationships are automatically deleted.
     */
    @Query(
        """
        DELETE FROM playlists
        WHERE id = :playlistId
        """
    )
    suspend fun deletePlaylist(
        playlistId: Long
    )


    // ---------------------------------------------------------
    // PLAYLIST SONGS
    // ---------------------------------------------------------

    /**
     * Add a song to a playlist.
     *
     * IGNORE prevents a duplicate playlist/song relationship.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addSongToPlaylist(
        playlistSong: PlaylistSongEntity
    )

    /**
     * Add multiple songs to a playlist.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addSongsToPlaylist(
        songs: List<PlaylistSongEntity>
    )

    /**
     * Remove a specific song from a playlist.
     */
    @Query(
        """
        DELETE FROM playlist_songs
        WHERE playlistId = :playlistId
        AND songId = :songId
        """
    )
    suspend fun removeSongFromPlaylist(
        playlistId: Long,
        songId: Long
    )

    /**
     * Remove all songs from a playlist.
     */
    @Query(
        """
        DELETE FROM playlist_songs
        WHERE playlistId = :playlistId
        """
    )
    suspend fun clearPlaylist(
        playlistId: Long
    )

    /**
     * Get all songs belonging to a playlist.
     *
     * Songs are returned in their playlist order.
     */
    @Query(
        """
        SELECT songs.*
        FROM songs
        INNER JOIN playlist_songs
            ON songs.id = playlist_songs.songId
        WHERE playlist_songs.playlistId = :playlistId
        ORDER BY playlist_songs.position ASC
        """
    )
    fun getPlaylistSongs(
        playlistId: Long
    ): Flow<List<SongEntity>>

    /**
     * Get playlist/song relationships.
     *
     * Useful when reordering a playlist.
     */
    @Query(
        """
        SELECT *
        FROM playlist_songs
        WHERE playlistId = :playlistId
        ORDER BY position ASC
        """
    )
    fun getPlaylistSongEntities(
        playlistId: Long
    ): Flow<List<PlaylistSongEntity>>

    /**
     * Update the position of a song inside a playlist.
     */
    @Query(
        """
        UPDATE playlist_songs
        SET position = :position
        WHERE playlistId = :playlistId
        AND songId = :songId
        """
    )
    suspend fun updateSongPosition(
        playlistId: Long,
        songId: Long,
        position: Int
    )

    /**
     * Get the number of songs in a playlist.
     */
    @Query(
        """
        SELECT COUNT(*)
        FROM playlist_songs
        WHERE playlistId = :playlistId
        """
    )
    fun getPlaylistSongCount(
        playlistId: Long
    ): Flow<Int>
}