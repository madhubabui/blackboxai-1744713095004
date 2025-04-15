package com.saver.statussaver.presentation.settings

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.saver.statussaver.R
import com.saver.statussaver.databinding.ActivitySettingsBinding
import com.saver.statussaver.presentation.tutorial.TutorialActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupThemeSelection()
        setupTutorialButton()
        observeViewModel()

        viewModel.loadSettings()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupThemeSelection() {
        binding.themeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val theme = when (checkedId) {
                R.id.themeLight -> AppTheme.LIGHT
                R.id.themeDark -> AppTheme.DARK
                R.id.themeSystem -> AppTheme.SYSTEM
                else -> AppTheme.SYSTEM
            }
            viewModel.setTheme(theme)
        }
    }

    private fun setupTutorialButton() {
        binding.tutorialCard.setOnClickListener {
            startActivity(Intent(this, TutorialActivity::class.java))
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                updateUiState(state)
            }
        }
    }

    private fun updateUiState(state: SettingsUiState) {
        // Update theme selection
        val themeButtonId = when (state.theme) {
            AppTheme.LIGHT -> R.id.themeLight
            AppTheme.DARK -> R.id.themeDark
            AppTheme.SYSTEM -> R.id.themeSystem
        }
        binding.themeRadioGroup.check(themeButtonId)

        // Update save location
        binding.saveLocationText.text = state.saveLocation

        // Apply theme
        val nightMode = when (state.theme) {
            AppTheme.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            AppTheme.DARK -> AppCompatDelegate.MODE_NIGHT_YES
            AppTheme.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        AppCompatDelegate.setDefaultNightMode(nightMode)
    }
}

enum class AppTheme {
    LIGHT,
    DARK,
    SYSTEM
}
