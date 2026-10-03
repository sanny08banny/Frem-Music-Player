package com.example.frem.repositoy

import com.example.frem.models.PlaylistDao
import com.example.frem.models.PlaylistEntity
import com.example.frem.models.PlaylistSongEntity
import com.example.frem.domain.model.Playlist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepository(
    private val playlistDao: PlaylistDao
) {

    fun getAllPlaylists(): Flow<List<Playlist>> {
        return playlistDao
            .getAllPlaylists()
            .map { playlists ->
                playlists.map {
                    it.toDomain()
                }
            }
    }

    suspend fun createPlaylist(
        name: String,
        description: String = ""
    ): Long {
        return playlistDao.createPlaylist(
            PlaylistEntity(
                name = name,
                description = description
            )
        )
    }

    suspend fun renamePlaylist(
        playlistId: Long,
        name: String
    ) {
        playlistDao.renamePlaylist(
            playlistId = playlistId,
            name = name
        )
    }

    suspend fun deletePlaylist(
        playlistId: Long
    ) {
        playlistDao.deletePlaylist(playlistId)
    }

    suspend fun addSong(
        playlistId: Long,
        songId: Long,
        position: Int
    ) {
        playlistDao.addSongToPlaylist(
            PlaylistSongEntity(
                playlistId = playlistId,
                songId = songId,
                position = position
            )
        )
    }

    fun getPlaylistSongs(
        playlistId: Long
    ) = playlistDao.getPlaylistSongs(playlistId)

    private fun PlaylistEntity.toDomain(): Playlist {
        return Playlist(
            id = id,
            name = name,
            description = description,
            dateCreated = dateCreated,
            dateModified = dateModified
        )
    }
}