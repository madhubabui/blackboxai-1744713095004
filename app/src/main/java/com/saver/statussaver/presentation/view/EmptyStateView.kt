package com.saver.statussaver.presentation.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.core.view.isVisible
import com.saver.statussaver.databinding.EmptyStateViewBinding

class EmptyStateView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = EmptyStateViewBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    init {
        isVisible = false
    }

    fun setup(
        @DrawableRes iconRes: Int,
        title: String,
        message: String,
        actionText: String? = null,
        onActionClick: (() -> Unit)? = null
    ) {
        with(binding) {
            emptyIcon.setImageResource(iconRes)
            titleText.text = title
            messageText.text = message

            if (actionText != null && onActionClick != null) {
                actionButton.apply {
                    text = actionText
                    setOnClickListener { onActionClick() }
                    isVisible = true
                }
            } else {
                actionButton.isVisible = false
            }
        }
    }

    fun showLoading(show: Boolean) {
        binding.apply {
            progressBar.isVisible = show
            contentGroup.isVisible = !show
        }
    }

    fun showError(
        title: String,
        message: String,
        actionText: String,
        onActionClick: () -> Unit
    ) {
        binding.apply {
            titleText.text = title
            messageText.text = message
            actionButton.apply {
                text = actionText
                setOnClickListener { onActionClick() }
                isVisible = true
            }
            progressBar.isVisible = false
            contentGroup.isVisible = true
        }
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        if (visibility == VISIBLE) {
            announceForAccessibility(
                "${binding.titleText.text}. ${binding.messageText.text}"
            )
        }
    }

    fun setAnimation(@DrawableRes animationRes: Int) {
        binding.emptyIcon.setImageResource(animationRes)
    }

    fun setIconTint(color: Int) {
        binding.emptyIcon.setColorFilter(color)
    }

    fun setTitleTextAppearance(textAppearance: Int) {
        binding.titleText.setTextAppearance(textAppearance)
    }

    fun setMessageTextAppearance(textAppearance: Int) {
        binding.messageText.setTextAppearance(textAppearance)
    }

    fun setActionButtonStyle(style: Int) {
        binding.actionButton.setTextAppearance(style)
    }

    fun setSpaceTop(space: Int) {
        binding.spaceTop.layoutParams.height = space
    }

    fun setSpaceBottom(space: Int) {
        binding.spaceBottom.layoutParams.height = space
    }

    fun setContentSpacing(space: Int) {
        binding.apply {
            titleText.setPadding(0, space, 0, 0)
            messageText.setPadding(0, space, 0, 0)
            actionButton.setPadding(0, space, 0, 0)
        }
    }
}
