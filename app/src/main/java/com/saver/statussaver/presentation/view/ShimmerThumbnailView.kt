package com.saver.statussaver.presentation.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.facebook.shimmer.ShimmerFrameLayout
import com.saver.statussaver.databinding.ShimmerThumbnailViewBinding

class ShimmerThumbnailView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = ShimmerThumbnailViewBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    private val shimmerContainer: ShimmerFrameLayout
        get() = binding.shimmerContainer

    init {
        // Set default aspect ratio to 1:1 (square)
        layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )
    }

    fun startShimmer() {
        shimmerContainer.startShimmer()
        isVisible = true
    }

    fun stopShimmer() {
        shimmerContainer.stopShimmer()
        isVisible = false
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

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (isVisible) {
            startShimmer()
        }
    }

    override fun onDetachedFromWindow() {
        stopShimmer()
        super.onDetachedFromWindow()
    }

    fun setShimmerColor(color: Int) {
        binding.shimmerContent.setBackgroundColor(color)
    }

    fun setShimmerAngle(angle: Int) {
        shimmerContainer.angle = angle
    }

    fun setShimmerDuration(duration: Long) {
        shimmerContainer.duration = duration
    }

    fun setShimmerRepeatCount(count: Int) {
        shimmerContainer.repeatCount = count
    }

    fun setShimmerRepeatMode(mode: Int) {
        shimmerContainer.repeatMode = mode
    }

    fun setShimmerBaseAlpha(alpha: Float) {
        shimmerContainer.baseAlpha = alpha
    }

    fun setShimmerHighlightAlpha(alpha: Float) {
        shimmerContainer.highlightAlpha = alpha
    }

    companion object {
        private const val DEFAULT_ASPECT_RATIO = 1.0f // Square by default
        private const val DEFAULT_SHIMMER_DURATION = 1000L
        private const val DEFAULT_SHIMMER_REPEAT_COUNT = -1 // Infinite
        private const val DEFAULT_SHIMMER_ANGLE = 0
        private const val DEFAULT_BASE_ALPHA = 0.3f
        private const val DEFAULT_HIGHLIGHT_ALPHA = 1.0f
    }
}
