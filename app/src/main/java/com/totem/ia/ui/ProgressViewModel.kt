package com.totem.ia.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.totem.ia.domain.model.Journey
import com.totem.ia.domain.repository.JourneyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.totem.ia.domain.model.UserJourneyState

sealed class ProgressUiState {
    object Loading : ProgressUiState()
    data class Success(
        val journeys: List<Journey>,
        val userStates: Map<String, UserJourneyState>,
        val totalStreak: Int
    ) : ProgressUiState()
    data class Error(val message: String) : ProgressUiState()
}

@HiltViewModel
class ProgressViewModel @Inject constructor(
    private val repository: JourneyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProgressUiState>(ProgressUiState.Loading)
    val uiState: StateFlow<ProgressUiState> = _uiState.asStateFlow()

    init {
        loadProgress()
    }

    fun loadProgress() {
        viewModelScope.launch {
            _uiState.value = ProgressUiState.Loading
            repository.getJourneys()
                .onSuccess { journeys ->
                    val states = mutableMapOf<String, UserJourneyState>()
                    var maxStreak = 0
                    
                    for (journey in journeys) {
                        repository.getUserJourneyState(journey.id).onSuccess { state ->
                            states[journey.id] = state
                            if (state.streak > maxStreak) maxStreak = state.streak
                        }
                    }

                    _uiState.value = ProgressUiState.Success(
                        journeys = journeys,
                        userStates = states,
                        totalStreak = maxStreak
                    )
                }
                .onFailure { error ->
                    _uiState.value = ProgressUiState.Error(error.localizedMessage ?: "Erro ao carregar progresso")
                }
        }
    }
}
