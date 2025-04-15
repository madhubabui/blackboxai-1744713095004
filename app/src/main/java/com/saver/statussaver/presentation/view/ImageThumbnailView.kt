package com.saver.statussaver.presentation.view

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.saver.statussaver.databinding.ImageThumbnailViewBinding

class ImageThumbnailView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = ImageThumbnailViewBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    init {
        // Set default aspect ratio to 1:1 (square)
        layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )
    }

    fun setImage(bitmap: Bitmap?) {
        binding.thumbnailImage.setImageBitmap(bitmap)
    }

    fun setSelected(selected: Boolean) {
        binding.selectionOverlay.isVisible = selected
        binding.checkIcon.isVisible = selected
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
            thumbnailImage.alpha = if (show) 0.5f else 1.0f
        }
    }

    fun setLoading(loading: Boolean) {
        binding.apply {
            progressBar.isVisible = loading
            thumbnailImage.alpha = if (loading) 0.5f else 1.0f
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

    fun setScaleType(scaleType: ScaleType) {
        binding.thumbnailImage.scaleType = when (scaleType) {
            ScaleType.CENTER_CROP -> android.widget.ImageView.ScaleType.CENTER_CROP
            ScaleType.FIT_CENTER -> android.widget.ImageView.ScaleType.FIT_CENTER
            ScaleType.CENTER_INSIDE -> android.widget.ImageView.ScaleType.CENTER_INSIDE
        }
    }

    enum class ScaleType {
        CENTER_CROP,
        FIT_CENTER,
        CENTER_INSIDE
    }

    companion object {
        private const val DEFAULT_ASPECT_RATIO = 1.0f // Square by default
    }
}
