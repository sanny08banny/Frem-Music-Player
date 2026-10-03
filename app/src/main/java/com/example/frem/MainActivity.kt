package com.example.frem

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.frem.utils.PermissionScreen
import com.example.frem.ui.theme.FremTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private var hasAudioPermission by mutableStateOf(false)

    private val audioPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->

            hasAudioPermission = granted

            if (granted) {
                scanMusic()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        enableEdgeToEdge()

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

        if (!hasAudioPermission) {
            requestAudioPermission()
        } else {
            scanMusic()
        }
    }

    private fun requestAudioPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            audioPermissionLauncher.launch(
                Manifest.permission.READ_MEDIA_AUDIO
            )

        } else {

            audioPermissionLauncher.launch(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }

    private fun checkAudioPermission(): Boolean {

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            checkSelfPermission(
                Manifest.permission.READ_MEDIA_AUDIO
            ) == PackageManager.PERMISSION_GRANTED

        } else {

            checkSelfPermission(
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun scanMusic() {

        val application =
            application as FremApplication

        CoroutineScope(
            Dispatchers.IO
        ).launch {

            application.musicRepository.scanMusic()
        }
    }
}