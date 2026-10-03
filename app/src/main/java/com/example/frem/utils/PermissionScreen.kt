package com.example.frem.utils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PermissionScreen(
    onRequestPermission: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.LibraryMusic,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Welcome to Frem",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 20.dp)
        )

        Text(
            text = "Frem needs access to your music library to find and play your songs.",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(
                top = 12.dp,
                bottom = 24.dp
            )
        )

        Button(
            onClick = onRequestPermission
        ) {
            Text("Allow Music Access")
        }
    }
}