package com.example.frem

import com.example.frem.data.media.FremPlayer
import com.example.frem.data.media.MusicScanner
import com.example.frem.data.repository.MusicRepository

import android.app.Application
import com.example.frem.repositoy.PlaylistRepository


class FremApplication : Application() {

    lateinit var database: FremDatabase
        private set

    lateinit var musicScanner: MusicScanner
        private set

    lateinit var musicRepository: MusicRepository
        private set
    lateinit var playlistRepository: PlaylistRepository
        private set
    lateinit var fremPlayer: FremPlayer
        private set

    override fun onCreate() {
        super.onCreate()

        // Room database
        database = FremDatabase.getInstance(this)

        // Music scanner
        musicScanner = MusicScanner(
            context = this,
            songDao = database.songDao()
        )

        // Music repository
        musicRepository = MusicRepository(
            songDao = database.songDao(),
            musicScanner = musicScanner
        )
        playlistRepository = PlaylistRepository(
            database.playlistDao()
        )
        // Media3 / ExoPlayer wrapper
        fremPlayer = FremPlayer(
            context = this
        )
    }
}