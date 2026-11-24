
package com.example.frem

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.example.frem.ui.theme.FremTheme
import com.example.frem.utils.PermissionScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    private var hasAudioPermission by mutableStateOf(false)

    private val audioPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->

            hasAudioPermission = granted

            if (granted) {
                requestNotificationPermission()
                scanMusic()
            }
        }

    private val notificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            // Notification permission is optional for music playback.
            // Continue using the app even if the user denies it.
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        hasAudioPermission = checkAudioPermission()

        setContent {
            FremTheme {
                if (hasAudioPermission) {
                    FremRoot()
                } else {
                    PermissionScreen(
                        onRequestPermission = {
                            requestAudioPermission()
                        }
                    )
                }
            }
        }

        if (hasAudioPermission) {
            requestNotificationPermission()
            scanMusic()
        } else {
            requestAudioPermission()
        }
    }

    private fun requestAudioPermission() {
        val permission = if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
        ) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        audioPermissionLauncher.launch(permission)
    }

    private fun checkAudioPermission(): Boolean {
        val permission = if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
        ) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        return checkSelfPermission(permission) ==
                PackageManager.PERMISSION_GRANTED
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (
                checkSelfPermission(
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }
        }
    }

    private fun scanMusic() {
        val application = application as FremApplication

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                application.musicRepository.scanMusic()
            }
        }
    }
}
