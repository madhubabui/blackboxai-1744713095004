package com.saver.statussaver.presentation.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.saver.statussaver.databinding.ActivitySplashBinding
import com.saver.statussaver.presentation.main.MainActivity
import com.saver.statussaver.presentation.tutorial.TutorialActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkInitialLaunch()
    }

    private fun checkInitialLaunch() {
        lifecycleScope.launch {
            // Add a small delay to show the splash screen
            delay(1500)

            if (viewModel.shouldShowTutorial()) {
                startTutorial()
            } else {
                startMainActivity()
            }
            finish()
        }
    }

    private fun startTutorial() {
        startActivity(Intent(this, TutorialActivity::class.java))
    }

    private fun startMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
    }
}
