package com.saver.statussaver.presentation.preview

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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreviewViewModel @Inject constructor(
    private val repository: StatusRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PreviewUiState>(PreviewUiState.Loading)
    val uiState: StateFlow<PreviewUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<PreviewEvent>()
    val events: SharedFlow<PreviewEvent> = _events.asSharedFlow()

    private var currentStatus: Status? = null

    fun setStatus(status: Status) {
        currentStatus = status
        _uiState.value = PreviewUiState.Success(status)
    }

    fun getStatus(): Status? = currentStatus

    fun saveStatus() {
        viewModelScope.launch {
            currentStatus?.let { status ->
                when (val result = repository.saveStatus(status)) {
                    is StatusResult.Success -> {
                        _events.emit(PreviewEvent.StatusSaved)
                        currentStatus = result.status
                    }
                    is StatusResult.Error -> {
                        _events.emit(PreviewEvent.ShowMessage(result.message))
                    }
                }
            }
        }
    }
}

sealed class PreviewUiState {
    object Loading : PreviewUiState()
    data class Success(val status: Status) : PreviewUiState()
    data class Error(val message: String) : PreviewUiState()
}

sealed class PreviewEvent {
    data class ShowMessage(val message: String) : PreviewEvent()
    object StatusSaved : PreviewEvent()
}
