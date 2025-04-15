package com.saver.statussaver.presentation.view

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.saver.statussaver.R
import com.saver.statussaver.databinding.ProgressBarViewBinding

class ProgressBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = ProgressBarViewBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    private var maxProgress = DEFAULT_MAX_PROGRESS
    private var currentProgress = 0
    private var showPercentage = true
    private var animationDuration = DEFAULT_ANIMATION_DURATION

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.ProgressBarView,
            defStyleAttr,
            0
        ).apply {
            try {
                showPercentage = getBoolean(R.styleable.ProgressBarView_showPercentage, true)
                maxProgress = getInteger(R.styleable.ProgressBarView_maxProgress, DEFAULT_MAX_PROGRESS)
                animationDuration = getInteger(
                    R.styleable.ProgressBarView_animationDuration,
                    DEFAULT_ANIMATION_DURATION
                )
                
                val progressTint = getColor(
                    R.styleable.ProgressBarView_progressTint,
                    ContextCompat.getColor(context, R.color.progress_tint)
                )
                val progressBackgroundTint = getColor(
                    R.styleable.ProgressBarView_progressBackgroundTint,
                    ContextCompat.getColor(context, R.color.progress_background_tint)
                )
                val progressMessage = getString(R.styleable.ProgressBarView_progressMessage)

                setupProgressBar(
                    progressTint,
                    progressBackgroundTint,
                    progressMessage
                )
            } finally {
                recycle()
            }
        }
    }

    private fun setupProgressBar(
        progressTint: Int,
        progressBackgroundTint: Int,
        progressMessage: String?
    ) {
        binding.apply {
            // Setup progress bar
            progressBar.apply {
                max = maxProgress
                progressTintList = ColorStateList.valueOf(progressTint)
                progressBackgroundTintList = ColorStateList.valueOf(progressBackgroundTint)
            }

            // Setup percentage text
            percentageText.isVisible = showPercentage

            // Setup message text
            progressMessage?.let {
                messageText.text = it
                messageText.isVisible = true
            } ?: run {
                messageText.isVisible = false
            }
        }
    }

    fun setProgress(progress: Int, animate: Boolean = true) {
        val targetProgress = progress.coerceIn(0, maxProgress)
        if (animate) {
            binding.progressBar.animate()
                .setDuration(animationDuration.toLong())
                .setUpdateListener { animation ->
                    val animatedValue = (animation.animatedValue as Float).toInt()
                    updateProgress(animatedValue)
                }
                .start()
        } else {
            updateProgress(targetProgress)
        }
    }

    private fun updateProgress(progress: Int) {
        currentProgress = progress
        binding.apply {
            progressBar.progress = progress
            if (showPercentage) {
                val percentage = ((progress.toFloat() / maxProgress) * 100).toInt()
                percentageText.text = context.getString(R.string.progress_percentage, percentage)
            }
        }
    }

    fun setMessage(message: String?) {
        binding.messageText.apply {
            text = message
            isVisible = !message.isNullOrEmpty()
        }
    }

    fun setShowPercentage(show: Boolean) {
        showPercentage = show
        binding.percentageText.isVisible = show
        if (show) {
            updateProgress(currentProgress)
        }
    }

    fun setProgressTint(color: Int) {
        binding.progressBar.progressTintList = ColorStateList.valueOf(color)
    }

    fun setProgressBackgroundTint(color: Int) {
        binding.progressBar.progressBackgroundTintList = ColorStateList.valueOf(color)
    }

    companion object {
        private const val DEFAULT_MAX_PROGRESS = 100
        private const val DEFAULT_ANIMATION_DURATION = 300
    }
}
