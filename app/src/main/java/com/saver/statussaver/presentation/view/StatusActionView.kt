package com.saver.statussaver.presentation.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.saver.statussaver.databinding.StatusActionViewBinding

class StatusActionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = StatusActionViewBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    private var onSaveClick: (() -> Unit)? = null
    private var onShareClick: (() -> Unit)? = null
    private var onDeleteClick: (() -> Unit)? = null

    init {
        setupClickListeners()
    }

    private fun setupClickListeners() {
        with(binding) {
            saveButton.setOnClickListener {
                onSaveClick?.invoke()
            }

            shareButton.setOnClickListener {
                onShareClick?.invoke()
            }

            deleteButton.setOnClickListener {
                onDeleteClick?.invoke()
            }
        }
    }

    fun setOnSaveClickListener(listener: () -> Unit) {
        onSaveClick = listener
    }

    fun setOnShareClickListener(listener: () -> Unit) {
        onShareClick = listener
    }

    fun setOnDeleteClickListener(listener: () -> Unit) {
        onDeleteClick = listener
    }

    fun setSaveEnabled(enabled: Boolean) {
        binding.saveButton.isEnabled = enabled
    }

    fun setShareEnabled(enabled: Boolean) {
        binding.shareButton.isEnabled = enabled
    }

    fun setDeleteEnabled(enabled: Boolean) {
        binding.deleteButton.isEnabled = enabled
    }

    fun showSaveButton(show: Boolean) {
        binding.saveButton.isVisible = show
    }

    fun showShareButton(show: Boolean) {
        binding.shareButton.isVisible = show
    }

    fun showDeleteButton(show: Boolean) {
        binding.deleteButton.isVisible = show
    }

    fun setSaveLoading(loading: Boolean) {
        binding.saveButton.apply {
            isEnabled = !loading
            alpha = if (loading) 0.5f else 1.0f
        }
    }

    fun setShareLoading(loading: Boolean) {
        binding.shareButton.apply {
            isEnabled = !loading
            alpha = if (loading) 0.5f else 1.0f
        }
    }

    fun setDeleteLoading(loading: Boolean) {
        binding.deleteButton.apply {
            isEnabled = !loading
            alpha = if (loading) 0.5f else 1.0f
        }
    }

    fun setSaveText(text: String) {
        binding.saveText.text = text
    }

    fun setShareText(text: String) {
        binding.shareText.text = text
    }

    fun setDeleteText(text: String) {
        binding.deleteText.text = text
    }

    fun setSaveIcon(iconRes: Int) {
        binding.saveIcon.setImageResource(iconRes)
    }

    fun setShareIcon(iconRes: Int) {
        binding.shareIcon.setImageResource(iconRes)
    }

    fun setDeleteIcon(iconRes: Int) {
        binding.deleteIcon.setImageResource(iconRes)
    }

    fun setIconTint(color: Int) {
        with(binding) {
            saveIcon.setColorFilter(color)
            shareIcon.setColorFilter(color)
            deleteIcon.setColorFilter(color)
        }
    }

    fun setTextColor(color: Int) {
        with(binding) {
            saveText.setTextColor(color)
            shareText.setTextColor(color)
            deleteText.setTextColor(color)
        }
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        with(binding) {
            saveButton.isEnabled = enabled
            shareButton.isEnabled = enabled
            deleteButton.isEnabled = enabled
        }
    }
}
