
package com.example.frem.presentation.queue

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Painter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val QueueBlack = Color(0xFF000000)
private val QueuePanel = Color(0xFF101010)
private val QueueGreen = Color(0xFF35E99A)
private val QueueWhite = Color(0xFFFFFFFF)
private val QueueGray = Color(0xFF888888)
private val QueueDarkGray = Color(0xFF292929)

@Composable
fun QueueScreen(
    state: QueueScreenState,
    onSelectQueue: (String) -> Unit,
    onCreateQueue: (String) -> Unit,
    onRenameQueue: (String, String) -> Unit,
    onDeleteQueue: (String) -> Unit,
    onSaveQueue: () -> Unit,
    onAddSongs: () -> Unit,
    onPlayQueue: () -> Unit,
    onPlaySong: (Long) -> Unit,
    onRemoveSong: (Long) -> Unit,
    onMoveSong: (Long, Int) -> Unit,
    onPlayNext: (Long) -> Unit,
    onClose: () -> Unit = {},
    onNavigate: (String) -> Unit = {}
) {
    val selectedQueue = state.selectedQueue

    var queueSelectorExpanded by remember { mutableStateOf(false) }
    var queueOptionsExpanded by remember { mutableStateOf(false) }

    var searchQuery by remember { mutableStateOf("") }

    var showCreateDialog by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val songs = remember(selectedQueue, searchQuery) {
        selectedQueue?.songs?.filter { song ->
            song.title.contains(searchQuery, ignoreCase = true) ||
                    song.artist.contains(searchQuery, ignoreCase = true) ||
                    song.album.contains(searchQuery, ignoreCase = true)
        } ?: emptyList()
    }

    val totalDuration = remember(selectedQueue) {
        selectedQueue?.songs?.sumOf { it.durationMs } ?: 0L
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(QueueBlack)
    ) {

        // Queue selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 5.dp, end = 7.dp, top = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(53.dp)
                        .clip(RoundedCornerShape(9.dp))
                        .border(
                            1.dp,
                            Color(0xFF454545),
                            RoundedCornerShape(9.dp)
                        )
                        .clickable {
                            queueSelectorExpanded = true
                        }
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedQueue?.name ?: "No queue selected",
                        color = QueueWhite,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Icon(
                        imageVector = Icons.Default.ExpandMore,
                        contentDescription = "Select queue",
                        tint = QueueWhite
                    )
                }

                DropdownMenu(
                    expanded = queueSelectorExpanded,
                    onDismissRequest = {
                        queueSelectorExpanded = false
                    },
                    modifier = Modifier.background(Color(0xFF202020))
                ) {
                    state.queues.forEach { queue ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    queue.name,
                                    color = QueueWhite
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.QueueMusic,
                                    contentDescription = null,
                                    tint = if (queue.id == state.selectedQueueId) {
                                        QueueGreen
                                    } else {
                                        QueueGray
                                    }
                                )
                            },
                            onClick = {
                                onSelectQueue(queue.id)
                                queueSelectorExpanded = false
                            }
                        )
                    }

                    HorizontalDivider(color = QueueDarkGray)

                    DropdownMenuItem(
                        text = {
                            Text("Create new queue", color = QueueGreen)
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                tint = QueueGreen
                            )
                        },
                        onClick = {
                            queueSelectorExpanded = false
                            showCreateDialog = true
                        }
                    )
                }
            }

            IconButton(onClick = onClose) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Close",
                    tint = QueueWhite,
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        // Queue actions and information
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(61.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onPlayQueue,
                enabled = !selectedQueue?.songs.isNullOrEmpty()
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = "Play queue",
                    tint = QueueWhite,
                    modifier = Modifier.size(28.dp)
                )
            }

            IconButton(
                onClick = onAddSongs,
                enabled = selectedQueue != null
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add songs",
                    tint = QueueWhite,
                    modifier = Modifier.size(25.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${(state.currentSongIndex + 1).coerceAtLeast(0)} / ${selectedQueue?.songs?.size ?: 0}",
                    color = QueueWhite,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "◷ ${formatQueueDuration(totalDuration)}",
                    color = QueueGray,
                    fontSize = 10.sp
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = onSaveQueue) {
                Icon(
                    Icons.Default.Save,
                    contentDescription = "Save queue",
                    tint = QueueWhite,
                    modifier = Modifier.size(24.dp)
                )
            }

            Box {
                IconButton(
                    onClick = {
                        queueOptionsExpanded = true
                    }
                ) {
                    Icon(
                        Icons.Default.MoreHoriz,
                        contentDescription = "Queue options",
                        tint = QueueWhite,
                        modifier = Modifier.size(27.dp)
                    )
                }

                DropdownMenu(
                    expanded = queueOptionsExpanded,
                    onDismissRequest = {
                        queueOptionsExpanded = false
                    },
                    modifier = Modifier.background(Color(0xFF202020))
                ) {
                    DropdownMenuItem(
                        text = {
                            Text("Add songs", color = QueueWhite)
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Add, null, tint = QueueWhite)
                        },
                        onClick = {
                            queueOptionsExpanded = false
                            onAddSongs()
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            Text("Save queue", color = QueueWhite)
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Save, null, tint = QueueWhite)
                        },
                        onClick = {
                            queueOptionsExpanded = false
                            onSaveQueue()
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            Text("Rename queue", color = QueueWhite)
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Edit, null, tint = QueueWhite)
                        },
                        enabled = selectedQueue != null,
                        onClick = {
                            queueOptionsExpanded = false
                            showRenameDialog = true
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            Text("Delete queue", color = Color(0xFFFF5252))
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Delete,
                                null,
                                tint = Color(0xFFFF5252)
                            )
                        },
                        enabled = selectedQueue != null,
                        onClick = {
                            queueOptionsExpanded = false
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }

        // Song list
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            state = rememberLazyListState(),
            contentPadding = PaddingValues(
                start = 5.dp,
                end = 5.dp,
                top = 2.dp,
                bottom = 8.dp
            ),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            itemsIndexed(
                items = songs,
                key = { _, song -> song.id }
            ) { index, song ->

                val isCurrentSong = song.id == state.currentSongId

                QueueSongItem(
                    song = song,
                    isCurrentSong = isCurrentSong,
                    isPlaying = state.isPlaying && isCurrentSong,
                    canMoveUp = index > 0,
                    canMoveDown = index < songs.lastIndex,
                    onClick = {
                        onPlaySong(song.id)
                    },
                    onPlayNext = {
                        onPlayNext(song.id)
                    },
                    onRemove = {
                        onRemoveSong(song.id)
                    },
                    onMoveUp = {
                        if (index > 0) {
                            onMoveSong(song.id, index - 1)
                        }
                    },
                    onMoveDown = {
                        if (index < songs.lastIndex) {
                            onMoveSong(song.id, index + 1)
                        }
                    }
                )
            }

            if (songs.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 65.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.QueueMusic,
                                contentDescription = null,
                                tint = QueueGray,
                                modifier = Modifier.size(38.dp)
                            )

                            Spacer(Modifier.height(12.dp))

                            Text(
                                text = if (searchQuery.isNotBlank()) {
                                    "No matching songs"
                                } else {
                                    "Your queue is empty"
                                },
                                color = QueueGray,
                                fontSize = 14.sp
                            )

                            if (searchQuery.isBlank() && selectedQueue != null) {
                                TextButton(onClick = onAddSongs) {
                                    Text(
                                        "Add songs",
                                        color = QueueGreen
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Search
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(49.dp)
                .background(Color(0xFF090909))
                .padding(horizontal = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Search,
                contentDescription = "Search",
                tint = QueueGray,
                modifier = Modifier.size(21.dp)
            )

            Spacer(Modifier.width(13.dp))

            BasicTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.weight(1f),
                singleLine = true,
                cursorBrush = SolidColor(QueueGreen),
                textStyle = TextStyle(
                    color = QueueWhite,
                    fontSize = 13.sp
                ),
                decorationBox = { innerTextField ->
                    Box {
                        if (searchQuery.isEmpty()) {
                            Text(
                                "Search in this queue...",
                                color = QueueGray,
                                fontSize = 13.sp
                            )
                        }
                        innerTextField()
                    }
                }
            )

            if (searchQuery.isNotEmpty()) {
                IconButton(
                    onClick = {
                        searchQuery = ""
                    }
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Clear search",
                        tint = QueueGray
                    )
                }
            }
        }

        QueueBottomNavigation(
            onNavigate = onNavigate
        )
    }

    // Create queue
    if (showCreateDialog) {
        QueueNameDialog(
            title = "Create queue",
            confirmText = "Create",
            onDismiss = {
                showCreateDialog = false
            },
            onConfirm = { name ->
                onCreateQueue(name)
                showCreateDialog = false
            }
        )
    }

    // Rename queue
    if (showRenameDialog && selectedQueue != null) {
        QueueNameDialog(
            title = "Rename queue",
            initialName = selectedQueue.name,
            confirmText = "Save",
            onDismiss = {
                showRenameDialog = false
            },
            onConfirm = { name ->
                onRenameQueue(selectedQueue.id, name)
                showRenameDialog = false
            }
        )
    }

    // Delete queue
    if (showDeleteDialog && selectedQueue != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
            },
            containerColor = Color(0xFF202020),
            title = {
                Text("Delete queue?", color = QueueWhite)
            },
            text = {
                Text(
                    "Delete \"${selectedQueue.name}\"?",
                    color = QueueGray
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteQueue(selectedQueue.id)
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete", color = Color(0xFFFF5252))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                    }
                ) {
                    Text("Cancel", color = QueueWhite)
                }
            }
        )
    }
}

@Composable
private fun QueueSongItem(
    song: QueueSongUi,
    isCurrentSong: Boolean,
    isPlaying: Boolean,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onClick: () -> Unit,
    onPlayNext: () -> Unit,
    onRemove: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    val titleColor = if (isCurrentSong) QueueGreen else QueueWhite
    val rowBackground = if (isCurrentSong) {
        Color(0xFF06130D)
    } else {
        QueueBlack
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clip(RoundedCornerShape(9.dp))
            .then(
                if (isCurrentSong) {
                    Modifier.border(
                        1.dp,
                        Color(0xFF23794E),
                        RoundedCornerShape(9.dp)
                    )
                } else {
                    Modifier
                }
            )
            .background(rowBackground)
            .clickable(onClick = onClick)
            .padding(horizontal = 9.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Reorder handle
        Icon(
            Icons.Default.Sort,
            contentDescription = "Reorder",
            tint = if (isCurrentSong) QueueGreen else QueueWhite,
            modifier = Modifier
                .width(30.dp)
                .size(21.dp)
        )

        Spacer(Modifier.width(8.dp))

        // Artwork
        if (song.artwork != null) {
            Image(
                painter = song.artwork,
                contentDescription = song.album,
                modifier = Modifier
                    .size(51.dp)
                    .clip(RoundedCornerShape(2.dp))
            )
        } else {
            Box(
                modifier = Modifier
                    .size(51.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(QueueDarkGray),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.QueueMusic,
                    contentDescription = null,
                    tint = QueueGray
                )
            }
        }

        Spacer(Modifier.width(12.dp))

        // Song details
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = song.title,
                color = titleColor,
                fontSize = 13.sp,
                fontWeight = if (isCurrentSong) {
                    FontWeight.Bold
                } else {
                    FontWeight.Normal
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = song.artist,
                color = if (isCurrentSong) QueueGreen else QueueWhite,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = song.album,
                color = if (isCurrentSong) QueueGreen else QueueGray,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(Modifier.width(4.dp))

        // Song menu and duration
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Center
        ) {
            Box {
                IconButton(
                    onClick = {
                        menuExpanded = true
                    },
                    modifier = Modifier.size(34.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(25.dp)
                            .clip(CircleShape)
                            .background(
                                if (isCurrentSong) QueueGreen else QueueWhite
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.MoreHoriz,
                            contentDescription = "Song options",
                            tint = QueueBlack,
                            modifier = Modifier.size(21.dp)
                        )
                    }
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = {
                        menuExpanded = false
                    },
                    modifier = Modifier.background(Color(0xFF202020))
                ) {
                    DropdownMenuItem(
                        text = {
                            Text("Play next", color = QueueWhite)
                        },
                        onClick = {
                            menuExpanded = false
                            onPlayNext()
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            Text("Move up", color = QueueWhite)
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.ArrowUpward,
                                null,
                                tint = QueueWhite
                            )
                        },
                        enabled = canMoveUp,
                        onClick = {
                            menuExpanded = false
                            onMoveUp()
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            Text("Move down", color = QueueWhite)
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.ArrowDownward,
                                null,
                                tint = QueueWhite
                            )
                        },
                        enabled = canMoveDown,
                        onClick = {
                            menuExpanded = false
                            onMoveDown()
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            Text("Remove from queue", color = Color(0xFFFF5252))
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Delete,
                                null,
                                tint = Color(0xFFFF5252)
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            onRemove()
                        }
                    )
                }
            }

            Text(
                text = formatQueueDuration(song.durationMs),
                color = if (isCurrentSong) QueueGreen else QueueGray,
                fontSize = 10.sp,
                modifier = Modifier.padding(end = 5.dp)
            )
        }
    }
}

@Composable
private fun QueueNameDialog(
    title: String,
    initialName: String = "",
    confirmText: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember(title, initialName) {
        mutableStateOf(initialName)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF202020),
        title = {
            Text(title, color = QueueWhite)
        },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                singleLine = true,
                placeholder = {
                    Text("Queue name", color = QueueGray)
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = QueueWhite,
                    unfocusedTextColor = QueueWhite,
                    focusedBorderColor = QueueGreen,
                    unfocusedBorderColor = QueueGray,
                    cursorColor = QueueGreen
                )
            )
        },
        confirmButton = {
            TextButton(
                enabled = name.isNotBlank(),
                onClick = {
                    onConfirm(name.trim())
                }
            ) {
                Text(confirmText, color = QueueGreen)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = QueueGray)
            }
        }
    )
}

@Composable
private fun QueueBottomNavigation(
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        "queue" to Icons.Default.QueueMusic,
        "playing" to Icons.Default.PlayArrow,
        "folders" to Icons.Default.Folder,
        "library" to Icons.Default.QueueMusic,
        "profile" to Icons.Default.MoreVert,
        "search" to Icons.Default.Search,
        "more" to Icons.Default.MoreHoriz
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF101010))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(Color(0xFF292929))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.12f)
                    .fillMaxHeight()
                    .background(QueueGreen)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(57.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { (route, icon) ->
                IconButton(
                    onClick = {
                        onNavigate(route)
                    }
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = route,
                        tint = if (route == "queue") {
                            QueueWhite
                        } else {
                            Color(0xFF777777)
                        },
                        modifier = Modifier.size(23.dp)
                    )
                }
            }
        }
    }
}

private fun formatQueueDuration(durationMs: Long): String {
    val totalSeconds = durationMs.coerceAtLeast(0L) / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return if (hours > 0) {
        "%d:%02d:%02d".format(hours, minutes, seconds)
    } else {
        "%d:%02d".format(minutes, seconds)
    }
}
