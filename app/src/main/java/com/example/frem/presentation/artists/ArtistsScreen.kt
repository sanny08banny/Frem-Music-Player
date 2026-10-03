package com.example.frem.presentation.artists


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.frem.domain.model.Artist

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun ArtistsScreen(
    viewModel: ArtistsViewModel,
    onBack: () -> Unit,
    onArtistClick: (Artist) -> Unit
) {
    val artists by viewModel.artists.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Artists")
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(
                horizontal = 16.dp,
                vertical = 8.dp
            ),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            items(
                artists,
                key = { it.id }
            ) { artist ->

                ListItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onArtistClick(artist)
                        },
                    leadingContent = {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null
                        )
                    },
                    headlineContent = {
                        Text(artist.name)
                    },
                    supportingContent = {
                        Text(
                            "${artist.albumCount} albums • " +
                                    "${artist.songCount} songs"
                        )
                    }
                )
            }
        }
    }
}