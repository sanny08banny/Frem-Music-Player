
package com.example.frem.presentation.queue

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun QueueRoute(
    viewModel: QueueViewModel,
    onAddSongs: () -> Unit,
    onClose: () -> Unit = {},
    onNavigate: (String) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    QueueScreen(
        state = state,

        onSelectQueue = viewModel::selectQueue,
        onCreateQueue = viewModel::createQueue,
        onRenameQueue = viewModel::renameQueue,
        onDeleteQueue = viewModel::deleteQueue,

        onSaveQueue = viewModel::saveQueue,
        onAddSongs = onAddSongs,
        onPlayQueue = viewModel::playQueue,
        onPlaySong = viewModel::playSong,
        onRemoveSong = viewModel::removeSong,
        onMoveSong = viewModel::moveSong,
        onPlayNext = viewModel::playNext,

        onClose = onClose,
        onNavigate = onNavigate
    )
}
