package com.saver.statussaver.presentation.recyclebin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saver.statussaver.data.model.Status
import com.saver.statussaver.data.model.StatusResult
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
class RecycleBinViewModel @Inject constructor(
    private val repository: StatusRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<RecycleBinUiState>(RecycleBinUiState.Loading)
    val uiState: StateFlow<RecycleBinUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<RecycleBinEvent>()
    val events: SharedFlow<RecycleBinEvent> = _events.asSharedFlow()

    private val selectedStatuses = mutableSetOf<Status>()

    fun loadRecycleBinStatuses() {
        viewModelScope.launch {
            _uiState.value = RecycleBinUiState.Loading

            try {
                repository.getRecycleBinStatuses().collect { statuses ->
                    _uiState.value = RecycleBinUiState.Success(
                        statuses = statuses,
                        isMultiSelectMode = selectedStatuses.isNotEmpty(),
                        selectedCount = selectedStatuses.size
                    )
                }
            } catch (e: Exception) {
                _uiState.value = RecycleBinUiState.Error(
                    e.message ?: "Failed to load recycle bin items"
                )
            }
        }
    }

    fun onStatusSelected(status: Status, isSelected: Boolean) {
        if (isSelected) {
            selectedStatuses.add(status)
        } else {
            selectedStatuses.remove(status)
        }
        updateSelectionState()
    }

    fun restoreSelectedStatuses() {
        viewModelScope.launch {
            val statusesToRestore = selectedStatuses.toList()
            var successCount = 0

            statusesToRestore.forEach { status ->
                when (repository.restoreFromRecycleBin(status)) {
                    is StatusResult.Success -> successCount++
                    is StatusResult.Error -> { /* Handle individual errors if needed */ }
                }
            }

            val message = when {
                successCount == statusesToRestore.size -> 
                    "Restored ${statusesToRestore.size} items"
                successCount > 0 -> 
                    "Restored $successCount of ${statusesToRestore.size} items"
                else -> 
                    "Failed to restore items"
            }

            _events.emit(RecycleBinEvent.ShowMessage(message))
            selectedStatuses.clear()
            updateSelectionState()
            loadRecycleBinStatuses()
        }
    }

    fun deleteSelectedStatuses() {
        viewModelScope.launch {
            val statusesToDelete = selectedStatuses.toList()
            var successCount = 0

            statusesToDelete.forEach { status ->
                when (repository.deleteFromRecycleBin(status)) {
                    is StatusResult.Success -> successCount++
                    is StatusResult.Error -> { /* Handle individual errors if needed */ }
                }
            }

            val message = when {
                successCount == statusesToDelete.size -> 
                    "Permanently deleted ${statusesToDelete.size} items"
                successCount > 0 -> 
                    "Deleted $successCount of ${statusesToDelete.size} items"
                else -> 
                    "Failed to delete items"
            }

            _events.emit(RecycleBinEvent.ShowMessage(message))
            selectedStatuses.clear()
            updateSelectionState()
            loadRecycleBinStatuses()
        }
    }

    private fun updateSelectionState() {
        val currentState = _uiState.value
        if (currentState is RecycleBinUiState.Success) {
            _uiState.update {
                currentState.copy(
                    isMultiSelectMode = selectedStatuses.isNotEmpty(),
                    selectedCount = selectedStatuses.size
                )
            }
        }
    }
}

sealed class RecycleBinUiState {
    object Loading : RecycleBinUiState()
    data class Success(
        val statuses: List<Status>,
        val isMultiSelectMode: Boolean,
        val selectedCount: Int
    ) : RecycleBinUiState()
    data class Error(val message: String) : RecycleBinUiState()
}

sealed class RecycleBinEvent {
    data class ShowMessage(val message: String) : RecycleBinEvent()
    object NavigateBack : RecycleBinEvent()
}
