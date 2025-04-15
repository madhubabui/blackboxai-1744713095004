package com.saver.statussaver.presentation.view

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.saver.statussaver.databinding.VideoThumbnailViewBinding
import com.saver.statussaver.util.VideoUtils

class VideoThumbnailView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = VideoThumbnailViewBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    init {
        // Set default aspect ratio to 16:9
        layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )
    }

    fun setThumbnail(bitmap: Bitmap?) {
        binding.thumbnailImage.setImageBitmap(bitmap)
    }

    fun setDuration(durationMs: Long) {
        if (durationMs > 0) {
            binding.durationText.apply {
                text = VideoUtils.formatDuration(durationMs)
                isVisible = true
            }
        } else {
            binding.durationText.isVisible = false
        }
    }

    fun setPlayButtonVisible(visible: Boolean) {
        binding.playButton.isVisible = visible
    }

    fun setSelected(selected: Boolean) {
        binding.selectionOverlay.isVisible = selected
        binding.checkIcon.isVisible = selected
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

    fun setError(show: Boolean) {
        binding.apply {
            errorIcon.isVisible = show
            playButton.isVisible = !show
            durationText.isVisible = !show
        }
    }

    fun setLoading(loading: Boolean) {
        binding.apply {
            progressBar.isVisible = loading
            playButton.isVisible = !loading
            durationText.isVisible = !loading
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
}
