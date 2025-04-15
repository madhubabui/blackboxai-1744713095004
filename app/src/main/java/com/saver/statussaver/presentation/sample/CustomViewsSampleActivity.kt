package com.saver.statussaver.presentation.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.saver.statussaver.R
import com.saver.statussaver.databinding.ActivityCustomViewsSampleBinding

class CustomViewsSampleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCustomViewsSampleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomViewsSampleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupViews()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = getString(R.string.title_custom_views_sample)
        }
    }

    private fun setupViews() {
        // Rating Bar
        binding.ratingBar.apply {
            rating = 4.5f
            setOnRatingChangeListener { rating ->
                // Handle rating change if needed
            }
        }

        // Segmented Control
        binding.segmentedControl.apply {
            segments = resources.getStringArray(R.array.time_periods).toList()
            selectedSegment = 0
            setOnSegmentSelectedListener { position ->
                // Handle segment selection if needed
            }
        }

        // Chip Group
        binding.chipGroup.apply {
            val chips = listOf(
                getString(R.string.chip_photos),
                getString(R.string.chip_videos),
                getString(R.string.chip_add_more)
            )
            setChips(chips)
            setOnChipSelectedListener { position, isSelected ->
                // Handle chip selection if needed
            }
        }

        // Badges
        binding.countBadge.apply {
            count = 5
        }

        binding.statusBadge.apply {
            status = "active"
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
