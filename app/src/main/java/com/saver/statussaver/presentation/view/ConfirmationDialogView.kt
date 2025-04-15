package com.saver.statussaver.presentation.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.saver.statussaver.R
import com.saver.statussaver.databinding.ConfirmationDialogViewBinding

class ConfirmationDialogView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = ConfirmationDialogViewBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    private var onConfirmListener: (() -> Unit)? = null
    private var onCancelListener: (() -> Unit)? = null
    private var onDismissListener: (() -> Unit)? = null

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.ConfirmationDialogView,
            defStyleAttr,
            0
        ).apply {
            try {
                val title = getString(R.styleable.ConfirmationDialogView_dialogTitle)
                val message = getString(R.styleable.ConfirmationDialogView_dialogMessage)
                val confirmText = getString(R.styleable.ConfirmationDialogView_confirmButtonText)
                val cancelText = getString(R.styleable.ConfirmationDialogView_cancelButtonText)
                val type = getInt(R.styleable.ConfirmationDialogView_dialogType, TYPE_DEFAULT)
                val showCancel = getBoolean(R.styleable.ConfirmationDialogView_showCancelButton, true)

                setupDialog(
                    title,
                    message,
                    confirmText,
                    cancelText,
                    type,
                    showCancel
                )
            } finally {
                recycle()
            }
        }
    }

    private fun setupDialog(
        title: String?,
        message: String?,
        confirmText: String?,
        cancelText: String?,
        type: Int,
        showCancel: Boolean
    ) {
        binding.apply {
            // Setup title
            titleText.apply {
                text = title
                isVisible = !title.isNullOrEmpty()
            }

            // Setup message
            messageText.apply {
                text = message
                isVisible = !message.isNullOrEmpty()
            }

            // Setup buttons
            confirmButton.text = confirmText ?: context.getString(R.string.confirm)
            cancelButton.apply {
                text = cancelText ?: context.getString(R.string.cancel)
                isVisible = showCancel
            }

            // Setup type-specific styling
            val (iconResId, iconTint) = when (type) {
                TYPE_WARNING -> Pair(
                    R.drawable.ic_warning,
                    R.color.dialog_warning_icon
                )
                TYPE_ERROR -> Pair(
                    R.drawable.ic_error,
                    R.color.dialog_error_icon
                )
                TYPE_SUCCESS -> Pair(
                    R.drawable.ic_check_circle,
                    R.color.dialog_success_icon
                )
                else -> Pair(
                    R.drawable.ic_info,
                    R.color.dialog_info_icon
                )
            }

            iconImage.apply {
                setImageResource(iconResId)
                setColorFilter(ContextCompat.getColor(context, iconTint))
            }

            // Setup click listeners
            confirmButton.setOnClickListener {
                onConfirmListener?.invoke()
                dismiss()
            }

            cancelButton.setOnClickListener {
                onCancelListener?.invoke()
                dismiss()
            }

            root.setOnClickListener {
                // Prevent clicks from passing through to views behind the dialog
            }

            backgroundView.setOnClickListener {
                dismiss()
            }
        }
    }

    fun show(
        title: String? = null,
        message: String? = null,
        confirmText: String? = null,
        cancelText: String? = null,
        type: Int = TYPE_DEFAULT,
        showCancel: Boolean = true
    ) {
        setupDialog(title, message, confirmText, cancelText, type, showCancel)
        isVisible = true
    }

    fun dismiss() {
        isVisible = false
        onDismissListener?.invoke()
    }

    fun setOnConfirmListener(listener: () -> Unit) {
        onConfirmListener = listener
    }

    fun setOnCancelListener(listener: () -> Unit) {
        onCancelListener = listener
    }

    fun setOnDismissListener(listener: () -> Unit) {
        onDismissListener = listener
    }

    companion object {
        const val TYPE_DEFAULT = 0
        const val TYPE_WARNING = 1
        const val TYPE_ERROR = 2
        const val TYPE_SUCCESS = 3
    }
}
