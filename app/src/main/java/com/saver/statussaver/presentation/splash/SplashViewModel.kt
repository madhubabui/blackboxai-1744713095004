package com.saver.statussaver.presentation.splash

import androidx.lifecycle.ViewModel
import com.saver.statussaver.data.preferences.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    suspend fun shouldShowTutorial(): Boolean {
        return !preferencesManager.isTutorialShown()
    }

    suspend fun cleanupRecycleBin() {
        val lastCleanup = preferencesManager.getLastCleanupTime()
        val currentTime = System.currentTimeMillis()
        
        // If last cleanup was more than 24 hours ago
        if (currentTime - lastCleanup > 24 * 60 * 60 * 1000) {
            // Update last cleanup time
            preferencesManager.setLastCleanupTime(currentTime)
            
            // TODO: Implement actual cleanup logic
            // This would typically be handled by the repository
            // repository.cleanupRecycleBin()
        }
    }
}
