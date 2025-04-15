package com.saver.statussaver.presentation.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.core.view.isVisible
import com.saver.statussaver.databinding.CustomToolbarViewBinding

class CustomToolbarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = CustomToolbarViewBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    private var onNavigationClick: (() -> Unit)? = null
    private var onActionClick: ((Int) -> Unit)? = null

    init {
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.navigationButton.setOnClickListener {
            onNavigationClick?.invoke()
        }
    }

    fun setTitle(title: String) {
        binding.titleText.text = title
    }

    fun setSubtitle(subtitle: String?) {
        binding.subtitleText.apply {
            text = subtitle
            isVisible = !subtitle.isNullOrEmpty()
        }
    }

    fun setNavigationIcon(@DrawableRes iconRes: Int) {
        binding.navigationButton.apply {
            setImageResource(iconRes)
            isVisible = true
        }
    }

    fun hideNavigation() {
        binding.navigationButton.isVisible = false
    }

    fun addAction(
        @DrawableRes iconRes: Int,
        contentDescription: String,
        position: Int = actions.size
    ) {
        val actionButton = LayoutInflater.from(context)
            .inflate(
                com.google.android.material.R.layout.design_menu_item_action_area,
                binding.actionsContainer,
                false
            ) as androidx.appcompat.widget.AppCompatImageView

        actionButton.apply {
            setImageResource(iconRes)
            this.contentDescription = contentDescription
            setOnClickListener { onActionClick?.invoke(position) }
        }

        binding.actionsContainer.addView(actionButton, position)
        actions.add(position, actionButton)
    }

    fun removeAction(position: Int) {
        if (position in 0 until actions.size) {
            binding.actionsContainer.removeViewAt(position)
            actions.removeAt(position)
        }
    }

    fun clearActions() {
        binding.actionsContainer.removeAllViews()
        actions.clear()
    }

    fun setActionEnabled(position: Int, enabled: Boolean) {
        actions.getOrNull(position)?.isEnabled = enabled
    }

    fun setActionVisible(position: Int, visible: Boolean) {
        actions.getOrNull(position)?.isVisible = visible
    }

    fun setActionTint(position: Int, color: Int) {
        actions.getOrNull(position)?.setColorFilter(color)
    }

    fun setNavigationOnClickListener(listener: () -> Unit) {
        onNavigationClick = listener
    }

    fun setActionOnClickListener(listener: (Int) -> Unit) {
        onActionClick = listener
    }

    fun setTitleTextAppearance(textAppearance: Int) {
        binding.titleText.setTextAppearance(textAppearance)
    }

    fun setSubtitleTextAppearance(textAppearance: Int) {
        binding.subtitleText.setTextAppearance(textAppearance)
    }

    fun setElevation(elevation: Float) {
        binding.root.elevation = elevation
    }

    fun setBackgroundColor(color: Int) {
        binding.root.setBackgroundColor(color)
    }

    companion object {
        private val actions = mutableListOf<androidx.appcompat.widget.AppCompatImageView>()
    }
}
