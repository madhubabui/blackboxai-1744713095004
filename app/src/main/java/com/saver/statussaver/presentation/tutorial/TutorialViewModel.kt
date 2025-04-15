package com.saver.statussaver.presentation.tutorial

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saver.statussaver.data.preferences.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TutorialViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    fun setTutorialCompleted() {
        viewModelScope.launch {
            preferencesManager.setTutorialShown(true)
        }
    }

    suspend fun shouldShowTutorial(): Boolean {
        return !preferencesManager.isTutorialShown()
    }
}
