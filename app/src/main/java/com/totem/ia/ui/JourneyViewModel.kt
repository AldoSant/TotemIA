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

sealed class JourneyUiState {
    object Loading : JourneyUiState()
    data class Success(
        val journeys: List<Journey>,
        val currentJourney: Pair<Journey, UserJourneyState>? = null,
        val recommendedJourney: Journey? = null
    ) : JourneyUiState()
    data class Error(val message: String) : JourneyUiState()
}

@HiltViewModel
class JourneyViewModel @Inject constructor(
    private val repository: JourneyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<JourneyUiState>(JourneyUiState.Loading)
    val uiState: StateFlow<JourneyUiState> = _uiState.asStateFlow()

    init {
        loadJourneys()
    }

    fun loadJourneys() {
        viewModelScope.launch {
            _uiState.value = JourneyUiState.Loading
            repository.getJourneys()
                .onSuccess { journeys ->
                    // Procura por jornada em andamento (simplificado para o MVP)
                    var current: Pair<Journey, UserJourneyState>? = null
                    for (j in journeys) {
                        val state = repository.getUserJourneyState(j.id).getOrNull()
                        if (state != null && state.progressPercent > 0 && state.progressPercent < 100) {
                            current = j to state
                            break
                        }
                    }
                    
                    val recommended = journeys.find { it.isRecommended } ?: journeys.firstOrNull()
                    
                    _uiState.value = JourneyUiState.Success(
                        journeys = journeys,
                        currentJourney = current,
                        recommendedJourney = recommended
                    )
                }
                .onFailure { error ->
                    _uiState.value = JourneyUiState.Error(error.localizedMessage ?: "Erro ao carregar jornadas")
                }
        }
    }
}
