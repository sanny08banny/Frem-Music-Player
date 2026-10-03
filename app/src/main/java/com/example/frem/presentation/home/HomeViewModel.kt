package com.example.frem.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frem.data.repository.MusicRepository
import com.example.frem.domain.model.Song
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(
    private val repository: MusicRepository
) : ViewModel() {

    val recentlyPlayed: StateFlow<List<Song>> =
        repository
            .getRecentlyPlayed()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )
}