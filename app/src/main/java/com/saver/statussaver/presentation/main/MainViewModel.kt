package com.saver.statussaver.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saver.statussaver.data.model.OperationType
import com.saver.statussaver.data.model.Status
import com.saver.statussaver.domain.repository.StatusRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: StatusRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.Loading)
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<MainEvent>()
    val events: SharedFlow<MainEvent> = _events.asSharedFlow()

    private var currentTab = 0
    private val selectedStatuses = mutableSetOf<Status>()

    fun loadStatuses() {
        viewModelScope.launch {
            _uiState.value = MainUiState.Loading

            try {
                if (!repository.isWhatsAppAvailable()) {
                    _uiState.value = MainUiState.Error("WhatsApp not found")
                    return@launch
                }

                repository.getStatuses().collect { statuses ->
                    if (statuses.isEmpty()) {
                        _uiState.value = MainUiState.Error("No statuses found")
                    } else {
                        _uiState.value = MainUiState.Success(
                            statuses = statuses,
                            isMultiSelectMode = selectedStatuses.isNotEmpty()
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = MainUiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun onTabSelected(position: Int) {
        currentTab = position
        // Reset selection when switching tabs
        selectedStatuses.clear()
        updateMultiSelectState()
    }

    fun onStatusSelected(status: Status, isSelected: Boolean) {
        if (isSelected) {
            selectedStatuses.add(status)
        } else {
            selectedStatuses.remove(status)
        }
        updateMultiSelectState()
    }

    fun saveSelectedStatuses() {
        viewModelScope.launch {
            val statusesToSave = selectedStatuses.toList()
            val results = repository.saveStatuses(statusesToSave)

            val successCount = results.count { it.result }
            val message = when {
                successCount == statusesToSave.size -> 
                    "Saved $successCount ${if (successCount == 1) "status" else "statuses"}"
                successCount > 0 -> 
                    "Saved $successCount of ${statusesToSave.size} statuses"
                else -> "Failed to save statuses"
            }

            _events.emit(MainEvent.ShowMessage(message))
            selectedStatuses.clear()
            updateMultiSelectState()
        }
    }

    fun onSettingsClicked() {
        viewModelScope.launch {
            _events.emit(MainEvent.NavigateToSettings)
        }
    }

    fun onRecycleBinClicked() {
        viewModelScope.launch {
            _events.emit(MainEvent.NavigateToRecycleBin)
        }
    }

    private fun updateMultiSelectState() {
        val currentState = _uiState.value
        if (currentState is MainUiState.Success) {
            _uiState.value = currentState.copy(
                isMultiSelectMode = selectedStatuses.isNotEmpty()
            )
        }
    }
}

sealed class MainUiState {
    object Loading : MainUiState()
    data class Success(
        val statuses: List<Status>,
        val isMultiSelectMode: Boolean
    ) : MainUiState()
    data class Error(val message: String) : MainUiState()
}

sealed class MainEvent {
    data class ShowMessage(val message: String) : MainEvent()
    object NavigateToSettings : MainEvent()
    object NavigateToRecycleBin : MainEvent()
}
