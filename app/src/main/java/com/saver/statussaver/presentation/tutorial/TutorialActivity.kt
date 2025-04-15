package com.saver.statussaver.presentation.tutorial

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.saver.statussaver.R
import com.saver.statussaver.databinding.ActivityTutorialBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TutorialActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTutorialBinding
    private val viewModel: TutorialViewModel by viewModels()
    private lateinit var tutorialAdapter: TutorialPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTutorialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewPager()
        setupButtons()
    }

    private fun setupViewPager() {
        tutorialAdapter = TutorialPagerAdapter(getTutorialPages())
        binding.viewPager.adapter = tutorialAdapter

        // Setup page indicator
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { _, _ -> }.attach()

        // Listen for page changes
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateButtonsForPage(position)
            }
        })
    }

    private fun setupButtons() {
        binding.skipButton.setOnClickListener {
            finishTutorial()
        }

        binding.nextButton.setOnClickListener {
            val currentItem = binding.viewPager.currentItem
            if (currentItem < tutorialAdapter.itemCount - 1) {
                binding.viewPager.currentItem = currentItem + 1
            } else {
                finishTutorial()
            }
        }

        updateButtonsForPage(0)
    }

    private fun updateButtonsForPage(position: Int) {
        val isLastPage = position == tutorialAdapter.itemCount - 1
        binding.nextButton.setText(if (isLastPage) R.string.onboarding_finish else R.string.onboarding_next)
        binding.skipButton.isVisible = !isLastPage
    }

    private fun getTutorialPages(): List<TutorialPage> {
        return listOf(
            TutorialPage(
                imageResId = R.drawable.tutorial_welcome,
                title = getString(R.string.onboarding_title_1),
                description = getString(R.string.onboarding_desc_1)
            ),
            TutorialPage(
                imageResId = R.drawable.tutorial_organize,
                title = getString(R.string.onboarding_title_2),
                description = getString(R.string.onboarding_desc_2)
            ),
            TutorialPage(
                imageResId = R.drawable.tutorial_privacy,
                title = getString(R.string.onboarding_title_3),
                description = getString(R.string.onboarding_desc_3)
            )
        )
    }

    private fun finishTutorial() {
        viewModel.setTutorialCompleted()
        finish()
    }
}

data class TutorialPage(
    val imageResId: Int,
    val title: String,
    val description: String
)
