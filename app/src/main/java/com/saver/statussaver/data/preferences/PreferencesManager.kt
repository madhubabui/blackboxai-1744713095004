package com.saver.statussaver.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.saver.statussaver.presentation.settings.AppTheme
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE
    )

    suspend fun setTheme(theme: AppTheme) {
        prefs.edit().putString(KEY_THEME, theme.name).apply()
    }

    suspend fun getTheme(): AppTheme {
        val themeName = prefs.getString(KEY_THEME, AppTheme.SYSTEM.name)
        return try {
            AppTheme.valueOf(themeName ?: AppTheme.SYSTEM.name)
        } catch (e: IllegalArgumentException) {
            AppTheme.SYSTEM
        }
    }

    suspend fun setTutorialShown(shown: Boolean) {
        prefs.edit().putBoolean(KEY_TUTORIAL_SHOWN, shown).apply()
    }

    suspend fun isTutorialShown(): Boolean {
        return prefs.getBoolean(KEY_TUTORIAL_SHOWN, false)
    }

    suspend fun setLastCleanupTime(time: Long) {
        prefs.edit().putLong(KEY_LAST_CLEANUP, time).apply()
    }

    suspend fun getLastCleanupTime(): Long {
        return prefs.getLong(KEY_LAST_CLEANUP, 0)
    }

    companion object {
        private const val PREFERENCES_NAME = "saver_preferences"
        private const val KEY_THEME = "theme"
        private const val KEY_TUTORIAL_SHOWN = "tutorial_shown"
        private const val KEY_LAST_CLEANUP = "last_cleanup"
    }
}
