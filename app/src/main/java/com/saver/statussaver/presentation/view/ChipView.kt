package com.saver.statussaver.presentation.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import com.google.android.material.chip.Chip
import com.saver.statussaver.R
import com.saver.statussaver.databinding.ChipViewBinding

class ChipView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.chipStyle
) : Chip(context, attrs, defStyleAttr) {

    private var onCloseListener: (() -> Unit)? = null
    private var onClickListener: (() -> Unit)? = null

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.ChipView,
            defStyleAttr,
            0
        ).apply {
            try {
                val text = getString(R.styleable.ChipView_chipText)
                val icon = getResourceId(R.styleable.ChipView_chipIcon, 0)
                val isCloseable = getBoolean(R.styleable.ChipView_isCloseable, false)
                val isCheckable = getBoolean(R.styleable.ChipView_isCheckable, false)
                val isChecked = getBoolean(R.styleable.ChipView_isChecked, false)
                val chipBackgroundColor = getColor(
                    R.styleable.ChipView_chipBackgroundColor,
                    ContextCompat.getColor(context, R.color.chip_background)
                )
                val chipTextColor = getColor(
                    R.styleable.ChipView_chipTextColor,
                    ContextCompat.getColor(context, R.color.chip_text)
                )
                val chipIconTint = getColor(
                    R.styleable.ChipView_chipIconTint,
                    ContextCompat.getColor(context, R.color.chip_icon)
                )

                setupChip(
                    text,
                    icon,
                    isCloseable,
                    isCheckable,
                    isChecked,
                    chipBackgroundColor,
                    chipTextColor,
                    chipIconTint
                )
            } finally {
                recycle()
            }
        }
    }

    private fun setupChip(
        text: String?,
        iconResId: Int,
        isCloseable: Boolean,
        isCheckable: Boolean,
        isChecked: Boolean,
        backgroundColor: Int,
        textColor: Int,
        iconTint: Int
    ) {
        // Set text
        this.text = text

        // Set icon if provided
        if (iconResId != 0) {
            setChipIconResource(iconResId)
            chipIconTint = android.content.res.ColorStateList.valueOf(iconTint)
            isChipIconVisible = true
        }

        // Set close icon
        isCloseIconVisible = isCloseable
        if (isCloseable) {
            setCloseIconResource(R.drawable.ic_close)
            closeIconTint = android.content.res.ColorStateList.valueOf(iconTint)
            setOnCloseIconClickListener { onCloseListener?.invoke() }
        }

        // Set checkable state
        this.isCheckable = isCheckable
        this.isChecked = isChecked

        // Set colors
        setChipBackgroundColorResource(backgroundColor)
        setTextColor(textColor)

        // Set click listener
        setOnClickListener { onClickListener?.invoke() }
    }

    fun setText(text: String) {
        this.text = text
    }

    fun setIcon(iconResId: Int, tint: Int? = null) {
        setChipIconResource(iconResId)
        isChipIconVisible = true
        tint?.let { chipIconTint = android.content.res.ColorStateList.valueOf(it) }
    }

    fun setCloseable(closeable: Boolean) {
        isCloseIconVisible = closeable
        if (closeable) {
            setCloseIconResource(R.drawable.ic_close)
            setOnCloseIconClickListener { onCloseListener?.invoke() }
        }
    }

    fun setChipBackgroundColor(color: Int) {
        chipBackgroundColor = android.content.res.ColorStateList.valueOf(color)
    }

    fun setOnCloseListener(listener: () -> Unit) {
        onCloseListener = listener
    }

    fun setOnChipClickListener(listener: () -> Unit) {
        onClickListener = listener
    }

    override fun setChecked(checked: Boolean) {
        if (isCheckable) {
            super.setChecked(checked)
        }
    }
}
