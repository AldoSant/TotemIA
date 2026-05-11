package com.totem.ia.ui

import androidx.lifecycle.SavedStateHandle
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

sealed class JourneyDetailUiState {
    object Loading : JourneyDetailUiState()
    data class Success(
        val journey: Journey,
        val userState: UserJourneyState? = null
    ) : JourneyDetailUiState()
    data class Error(val message: String) : JourneyDetailUiState()
}

@HiltViewModel
class JourneyDetailViewModel @Inject constructor(
    private val repository: JourneyRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val journeyId: String = checkNotNull(savedStateHandle["id"])

    private val _uiState = MutableStateFlow<JourneyDetailUiState>(JourneyDetailUiState.Loading)
    val uiState: StateFlow<JourneyDetailUiState> = _uiState.asStateFlow()

    init {
        loadJourneyDetails()
    }

    fun loadJourneyDetails() {
        viewModelScope.launch {
            _uiState.value = JourneyDetailUiState.Loading
            val journeyResult = repository.getJourneyDetails(journeyId)
            val stateResult = repository.getUserJourneyState(journeyId)
            
            if (journeyResult.isSuccess) {
                _uiState.value = JourneyDetailUiState.Success(
                    journey = journeyResult.getOrThrow(),
                    userState = stateResult.getOrNull()
                )
            } else {
                _uiState.value = JourneyDetailUiState.Error(
                    journeyResult.exceptionOrNull()?.localizedMessage ?: "Erro ao carregar detalhes"
                )
            }
        }
    }
}
