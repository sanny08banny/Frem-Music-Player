package com.example.frem.presentation.folders


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frem.data.repository.MusicRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.io.File

class FoldersViewModel(
    repository: MusicRepository
) : ViewModel() {

    val folders: StateFlow<List<MusicFolder>> =
        repository.getAllSongs()
            .map { songs ->

                songs
                    .groupBy { song ->

                        val file = File(song.path)

                        file.parent ?: ""
                    }
                    .filterKeys {
                        it.isNotBlank()
                    }
                    .map { (path, songsInFolder) ->

                        MusicFolder(
                            path = path,
                            name = File(path).name.ifBlank {
                                path
                            },
                            songCount = songsInFolder.size
                        )
                    }
                    .sortedBy {
                        it.name.lowercase()
                    }

            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyList()
            )
}