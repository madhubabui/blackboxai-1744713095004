package com.saver.statussaver.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saver.statussaver.data.preferences.PreferencesManager
import com.saver.statussaver.domain.repository.StatusRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val repository: StatusRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun loadSettings() {
        viewModelScope.launch {
            _uiState.value = SettingsUiState(
                theme = preferencesManager.getTheme(),
                saveLocation = repository.getSavedStatusesPath()
            )
        }
    }

    fun setTheme(theme: AppTheme) {
        viewModelScope.launch {
            preferencesManager.setTheme(theme)
            _uiState.value = _uiState.value.copy(theme = theme)
        }
    }
}

data class SettingsUiState(
    val theme: AppTheme = AppTheme.SYSTEM,
    val saveLocation: String = ""
)
