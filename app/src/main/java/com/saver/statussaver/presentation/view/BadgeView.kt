package com.saver.statussaver.presentation.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.saver.statussaver.R
import com.saver.statussaver.databinding.BadgeViewBinding

class BadgeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = BadgeViewBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    private var maxCount = DEFAULT_MAX_COUNT
    private var badgeType = TYPE_COUNT
    private var currentCount = 0
    private var currentStatus = STATUS_NONE

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.BadgeView,
            defStyleAttr,
            0
        ).apply {
            try {
                maxCount = getInteger(R.styleable.BadgeView_maxCount, DEFAULT_MAX_COUNT)
                badgeType = getInteger(R.styleable.BadgeView_badgeType, TYPE_COUNT)
                currentStatus = getInteger(R.styleable.BadgeView_status, STATUS_NONE)
                
                val badgeColor = getColor(
                    R.styleable.BadgeView_badgeColor,
                    ContextCompat.getColor(context, R.color.badge_background)
                )
                val textColor = getColor(
                    R.styleable.BadgeView_badgeTextColor,
                    ContextCompat.getColor(context, R.color.badge_text)
                )
                val minWidth = getDimensionPixelSize(
                    R.styleable.BadgeView_badgeMinWidth,
                    resources.getDimensionPixelSize(R.dimen.badge_min_width)
                )
                val minHeight = getDimensionPixelSize(
                    R.styleable.BadgeView_badgeMinHeight,
                    resources.getDimensionPixelSize(R.dimen.badge_min_height)
                )
                val padding = getDimensionPixelSize(
                    R.styleable.BadgeView_badgePadding,
                    resources.getDimensionPixelSize(R.dimen.badge_padding)
                )
                val textSize = getDimensionPixelSize(
                    R.styleable.BadgeView_badgeTextSize,
                    resources.getDimensionPixelSize(R.dimen.badge_text_size)
                ).toFloat()
                val elevation = getDimensionPixelSize(
                    R.styleable.BadgeView_badgeElevation,
                    resources.getDimensionPixelSize(R.dimen.badge_elevation)
                ).toFloat()

                setupBadge(
                    badgeColor,
                    textColor,
                    minWidth,
                    minHeight,
                    padding,
                    textSize,
                    elevation
                )
            } finally {
                recycle()
            }
        }
    }

    private fun setupBadge(
        badgeColor: Int,
        textColor: Int,
        minWidth: Int,
        minHeight: Int,
        padding: Int,
        textSize: Float,
        elevation: Float
    ) {
        binding.root.apply {
            minimumWidth = minWidth
            minimumHeight = minHeight
            setPadding(padding, padding, padding, padding)
            this@BadgeView.elevation = elevation

            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                color = ColorStateList.valueOf(badgeColor)
            }
        }

        binding.badgeText.apply {
            setTextColor(textColor)
            textSize = textSize
        }

        updateBadgeContent()
    }

    private fun updateBadgeContent() {
        when (badgeType) {
            TYPE_COUNT -> {
                binding.badgeText.text = when {
                    currentCount == 0 -> ""
                    currentCount > maxCount -> "$maxCount+"
                    else -> currentCount.toString()
                }
                visibility = if (currentCount > 0) VISIBLE else GONE
            }
            TYPE_STATUS -> {
                binding.badgeText.text = when (currentStatus) {
                    STATUS_ACTIVE -> context.getString(R.string.badge_status_active)
                    STATUS_WARNING -> context.getString(R.string.badge_status_warning)
                    STATUS_ERROR -> context.getString(R.string.badge_status_error)
                    else -> ""
                }
                visibility = if (currentStatus != STATUS_NONE) VISIBLE else GONE
            }
        }
    }

    var count: Int
        get() = currentCount
        set(value) {
            if (badgeType == TYPE_COUNT) {
                currentCount = value.coerceAtLeast(0)
                updateBadgeContent()
            }
        }

    var status: Int
        get() = currentStatus
        set(value) {
            if (badgeType == TYPE_STATUS && value in STATUS_NONE..STATUS_ERROR) {
                currentStatus = value
                updateBadgeContent()
            }
        }

    fun setBadgeColor(color: Int) {
        (binding.root.background as? GradientDrawable)?.color = ColorStateList.valueOf(color)
    }

    fun setTextColor(color: Int) {
        binding.badgeText.setTextColor(color)
    }

    companion object {
        private const val DEFAULT_MAX_COUNT = 99
        const val TYPE_COUNT = 0
        const val TYPE_STATUS = 1
        const val STATUS_NONE = 0
        const val STATUS_ACTIVE = 1
        const val STATUS_WARNING = 2
        const val STATUS_ERROR = 3
    }
}
