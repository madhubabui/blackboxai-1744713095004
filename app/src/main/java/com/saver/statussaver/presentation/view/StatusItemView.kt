package com.saver.statussaver.presentation.view

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.saver.statussaver.databinding.StatusItemViewBinding
import com.saver.statussaver.util.VideoUtils
import java.io.File

class StatusItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = StatusItemViewBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    private var isVideo = false
    private var isSelected = false
    private var isLoading = false
    private var hasError = false

    init {
        layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )
    }

    fun setImage(bitmap: Bitmap?) {
        binding.thumbnailImage.setImageBitmap(bitmap)
    }

    fun setVideo(file: File, thumbnail: Bitmap?) {
        isVideo = true
        binding.apply {
            thumbnailImage.setImageBitmap(thumbnail)
            playButton.isVisible = true
            
            // Get and display video duration
            val duration = VideoUtils.getVideoDuration(context, file)
            if (duration > 0) {
                durationText.apply {
                    text = VideoUtils.formatDuration(duration)
                    isVisible = true
                }
            }
        }
    }

    fun setSelected(selected: Boolean) {
        isSelected = selected
        updateSelectionState()
    }

    fun setLoading(loading: Boolean) {
        isLoading = loading
        updateLoadingState()
    }

    fun setError(error: Boolean) {
        hasError = error
        updateErrorState()
    }

    private fun updateSelectionState() {
        binding.apply {
            selectionOverlay.isVisible = isSelected
            checkIcon.isVisible = isSelected
        }
    }

    private fun updateLoadingState() {
        binding.apply {
            progressBar.isVisible = isLoading
            contentGroup.alpha = if (isLoading) 0.5f else 1.0f
            isEnabled = !isLoading
        }
    }

    private fun updateErrorState() {
        binding.apply {
            errorIcon.isVisible = hasError
            contentGroup.alpha = if (hasError) 0.5f else 1.0f
            playButton.isVisible = isVideo && !hasError
            durationText.isVisible = isVideo && !hasError
        }
    }

    fun setOnPlayButtonClickListener(listener: OnClickListener) {
        binding.playButton.setOnClickListener(listener)
    }

    override fun setOnClickListener(listener: OnClickListener?) {
        binding.root.setOnClickListener(listener)
    }

    override fun setOnLongClickListener(listener: OnLongClickListener?) {
        binding.root.setOnLongClickListener(listener)
    }

    fun setAspectRatio(width: Int, height: Int) {
        if (width > 0 && height > 0) {
            val aspectRatio = width.toFloat() / height.toFloat()
            layoutParams = layoutParams.apply {
                this.height = (measuredWidth / aspectRatio).toInt()
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        // Ensure the view maintains its aspect ratio
        val width = measuredWidth
        val height = measuredHeight
        if (width > 0 && height > 0) {
            val aspectRatio = width.toFloat() / height.toFloat()
            val newHeight = (width / aspectRatio).toInt()
            setMeasuredDimension(width, newHeight)
        }
    }

    companion object {
        private const val DEFAULT_ASPECT_RATIO = 1.0f // Square by default
    }
}
