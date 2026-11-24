
package com.example.frem.data.media

import android.content.Intent
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.example.frem.FremApplication

class FremMediaService : MediaSessionService() {

    private var mediaSession: MediaSession? = null
    private lateinit var fremPlayer: FremPlayer

    override fun onCreate() {
        super.onCreate()

        fremPlayer = (application as FremApplication).fremPlayer

        mediaSession = MediaSession.Builder(
            this,
            fremPlayer.getPlayer()
        ).build()
    }

    override fun onGetSession(
        controllerInfo: MediaSession.ControllerInfo
    ): MediaSession? {
        return mediaSession
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        if (fremPlayer.isPlayingNow()) {
            return
        }

        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        mediaSession?.release()
        mediaSession = null

        // Do not release the Application-owned player here.
        super.onDestroy()
    }
}
