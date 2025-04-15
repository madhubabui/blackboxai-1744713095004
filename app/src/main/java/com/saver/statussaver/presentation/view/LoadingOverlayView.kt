package com.saver.statussaver.presentation.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.saver.statussaver.R
import com.saver.statussaver.databinding.LoadingOverlayViewBinding

class LoadingOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = LoadingOverlayViewBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    private var message: String? = null
    private var showProgress = true

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.LoadingOverlayView,
            defStyleAttr,
            0
        ).apply {
            try {
                message = getString(R.styleable.LoadingOverlayView_loadingMessage)
                showProgress = getBoolean(R.styleable.LoadingOverlayView_showProgress, true)
                val dimBackground = getBoolean(R.styleable.LoadingOverlayView_dimBackground, true)
                val cancelable = getBoolean(R.styleable.LoadingOverlayView_cancelable, false)
                val progressSize = getInt(R.styleable.LoadingOverlayView_progressSize, PROGRESS_SIZE_NORMAL)

                setupOverlay(
                    message,
                    showProgress,
                    dimBackground,
                    cancelable,
                    progressSize
                )
            } finally {
                recycle()
            }
        }
    }

    private fun setupOverlay(
        message: String?,
        showProgress: Boolean,
        dimBackground: Boolean,
        cancelable: Boolean,
        progressSize: Int
    ) {
        binding.apply {
            // Setup message
            messageText.apply {
                text = message
                isVisible = !message.isNullOrEmpty()
            }

            // Setup progress
            progressBar.apply {
                isVisible = showProgress
                val size = when (progressSize) {
                    PROGRESS_SIZE_SMALL -> resources.getDimensionPixelSize(R.dimen.progress_size_small)
                    PROGRESS_SIZE_LARGE -> resources.getDimensionPixelSize(R.dimen.progress_size_large)
                    else -> resources.getDimensionPixelSize(R.dimen.progress_size_normal)
                }
                layoutParams = layoutParams.apply {
                    width = size
                    height = size
                }
            }

            // Setup background
            root.setBackgroundResource(
                if (dimBackground) R.color.overlay_dim_background
                else android.R.color.transparent
            )

            // Setup click listener for cancelable
            if (cancelable) {
                root.setOnClickListener { hide() }
            }
        }
    }

    fun show(message: String? = null) {
        this.message = message
        binding.messageText.apply {
            text = message
            isVisible = !message.isNullOrEmpty()
        }
        isVisible = true
    }

    fun hide() {
        isVisible = false
    }

    fun setMessage(message: String?) {
        this.message = message
        binding.messageText.apply {
            text = message
            isVisible = !message.isNullOrEmpty()
        }
    }

    fun showProgress(show: Boolean) {
        showProgress = show
        binding.progressBar.isVisible = show
    }

    fun setProgressSize(size: Int) {
        val dimension = when (size) {
            PROGRESS_SIZE_SMALL -> R.dimen.progress_size_small
            PROGRESS_SIZE_LARGE -> R.dimen.progress_size_large
            else -> R.dimen.progress_size_normal
        }
        
        binding.progressBar.layoutParams = binding.progressBar.layoutParams.apply {
            val newSize = resources.getDimensionPixelSize(dimension)
            width = newSize
            height = newSize
        }
    }

    companion object {
        const val PROGRESS_SIZE_SMALL = 0
        const val PROGRESS_SIZE_NORMAL = 1
        const val PROGRESS_SIZE_LARGE = 2
    }
}
