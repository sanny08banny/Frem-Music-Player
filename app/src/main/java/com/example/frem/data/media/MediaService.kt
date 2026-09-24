package com.example.frem.data.media


import android.content.Intent
import androidx.media3.common.Player
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService

class FremMediaService : MediaSessionService() {

    private var mediaSession: MediaSession? = null

    private lateinit var fremPlayer: FremPlayer

    override fun onCreate() {
        super.onCreate()

        fremPlayer = FremPlayer(applicationContext)

        mediaSession = MediaSession.Builder(
            this,
            fremPlayer.getPlayer()
        )
            .setCallback(
                object : MediaSession.Callback {
                    // Media3 handles the standard playback commands
                    // through the Player automatically.
                }
            )
            .build()
    }

    override fun onGetSession(
        controllerInfo: MediaSession.ControllerInfo
    ): MediaSession? {
        return mediaSession
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)

        /*
         * Keep playback alive when the user removes Frem
         * from the recent-apps screen while music is playing.
         */
        if (fremPlayer.isPlayingNow()) {
            return
        }

        stopSelf()
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.stop()
            release()
        }

        mediaSession = null

        fremPlayer.release()

        super.onDestroy()
    }
}