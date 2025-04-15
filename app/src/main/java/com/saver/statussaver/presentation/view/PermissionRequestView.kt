package com.saver.statussaver.presentation.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.core.view.isVisible
import com.saver.statussaver.databinding.PermissionRequestViewBinding

class PermissionRequestView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = PermissionRequestViewBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    private var onGrantClick: (() -> Unit)? = null
    private var onDenyClick: (() -> Unit)? = null
    private var onSettingsClick: (() -> Unit)? = null

    init {
        setupClickListeners()
    }

    private fun setupClickListeners() {
        with(binding) {
            grantButton.setOnClickListener {
                onGrantClick?.invoke()
            }

            denyButton.setOnClickListener {
                onDenyClick?.invoke()
            }

            settingsButton.setOnClickListener {
                onSettingsClick?.invoke()
            }
        }
    }

    fun setup(
        @DrawableRes iconRes: Int,
        title: String,
        message: String,
        grantText: String = "Grant Permission",
        denyText: String = "Not Now",
        showSettings: Boolean = false,
        settingsText: String = "Open Settings",
        onGrant: () -> Unit,
        onDeny: () -> Unit,
        onSettings: (() -> Unit)? = null
    ) {
        with(binding) {
            permissionIcon.setImageResource(iconRes)
            titleText.text = title
            messageText.text = message
            grantButton.text = grantText
            denyButton.text = denyText
            settingsButton.apply {
                text = settingsText
                isVisible = showSettings
            }
        }

        this.onGrantClick = onGrant
        this.onDenyClick = onDeny
        this.onSettingsClick = onSettings
    }

    fun showRationale(
        message: String,
        showSettings: Boolean = false
    ) {
        with(binding) {
            messageText.text = message
            settingsButton.isVisible = showSettings
            denyButton.isVisible = !showSettings
            grantButton.isVisible = !showSettings
        }
    }

    fun showError(
        message: String,
        actionText: String = "Try Again",
        onAction: () -> Unit
    ) {
        with(binding) {
            messageText.text = message
            grantButton.apply {
                text = actionText
                setOnClickListener { onAction() }
            }
            denyButton.isVisible = false
            settingsButton.isVisible = false
        }
    }

    fun setIconTint(color: Int) {
        binding.permissionIcon.setColorFilter(color)
    }

    fun setTitleTextAppearance(textAppearance: Int) {
        binding.titleText.setTextAppearance(textAppearance)
    }

    fun setMessageTextAppearance(textAppearance: Int) {
        binding.messageText.setTextAppearance(textAppearance)
    }

    fun setGrantButtonStyle(style: Int) {
        binding.grantButton.setTextAppearance(style)
    }

    fun setDenyButtonStyle(style: Int) {
        binding.denyButton.setTextAppearance(style)
    }

    fun setSettingsButtonStyle(style: Int) {
        binding.settingsButton.setTextAppearance(style)
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        with(binding) {
            grantButton.isEnabled = enabled
            denyButton.isEnabled = enabled
            settingsButton.isEnabled = enabled
        }
    }

    fun setLoading(loading: Boolean) {
        with(binding) {
            progressBar.isVisible = loading
            contentGroup.isVisible = !loading
        }
    }
}
