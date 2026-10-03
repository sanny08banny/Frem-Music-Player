package com.example.frem.presentation.folders


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Folder
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

data class MusicFolder(
    val path: String,
    val name: String,
    val songCount: Int
)

@Composable
fun FoldersScreen(
    viewModel: FoldersViewModel,
    onBack: () -> Unit,
    onFolderClick: (MusicFolder) -> Unit
) {
    val folders by viewModel.folders.collectAsState()

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = {
                    Text("Folders")
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
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            items(
                folders,
                key = { it.path }
            ) { folder ->

                ListItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onFolderClick(folder)
                        },
                    leadingContent = {
                        Icon(
                            Icons.Default.Folder,
                            contentDescription = null
                        )
                    },
                    headlineContent = {
                        Text(folder.name)
                    },
                    supportingContent = {
                        Text("${folder.songCount} songs")
                    }
                )
            }
        }
    }
}