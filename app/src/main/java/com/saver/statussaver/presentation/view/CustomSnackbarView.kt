package com.saver.statussaver.presentation.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.saver.statussaver.R
import com.saver.statussaver.databinding.CustomSnackbarViewBinding

class CustomSnackbarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = CustomSnackbarViewBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    private var duration = BaseTransientBottomBar.LENGTH_SHORT
    private var actionClickListener: (() -> Unit)? = null
    private var dismissCallback: (() -> Unit)? = null

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.CustomSnackbarView,
            defStyleAttr,
            0
        ).apply {
            try {
                val message = getString(R.styleable.CustomSnackbarView_snackbarMessage)
                val actionText = getString(R.styleable.CustomSnackbarView_actionText)
                val type = getInt(R.styleable.CustomSnackbarView_snackbarType, TYPE_INFO)
                duration = getInt(
                    R.styleable.CustomSnackbarView_duration,
                    BaseTransientBottomBar.LENGTH_SHORT
                )

                setupSnackbar(
                    message,
                    actionText,
                    type
                )
            } finally {
                recycle()
            }
        }
    }

    private fun setupSnackbar(
        message: String?,
        actionText: String?,
        type: Int
    ) {
        binding.apply {
            // Setup message
            messageText.text = message

            // Setup action button
            actionButton.apply {
                text = actionText
                isVisible = !actionText.isNullOrEmpty()
                setOnClickListener { actionClickListener?.invoke() }
            }

            // Setup type-specific styling
            val (backgroundColor, iconResId) = when (type) {
                TYPE_SUCCESS -> Pair(
                    R.color.snackbar_success_background,
                    R.drawable.ic_check_circle
                )
                TYPE_ERROR -> Pair(
                    R.color.snackbar_error_background,
                    R.drawable.ic_error
                )
                TYPE_WARNING -> Pair(
                    R.color.snackbar_warning_background,
                    R.drawable.ic_warning
                )
                else -> Pair(
                    R.color.snackbar_info_background,
                    R.drawable.ic_info
                )
            }

            root.setCardBackgroundColor(ContextCompat.getColor(context, backgroundColor))
            typeIcon.setImageResource(iconResId)
        }
    }

    fun show(message: String, actionText: String? = null, type: Int = TYPE_INFO) {
        binding.apply {
            messageText.text = message
            actionButton.apply {
                text = actionText
                isVisible = !actionText.isNullOrEmpty()
            }

            val (backgroundColor, iconResId) = when (type) {
                TYPE_SUCCESS -> Pair(
                    R.color.snackbar_success_background,
                    R.drawable.ic_check_circle
                )
                TYPE_ERROR -> Pair(
                    R.color.snackbar_error_background,
                    R.drawable.ic_error
                )
                TYPE_WARNING -> Pair(
                    R.color.snackbar_warning_background,
                    R.drawable.ic_warning
                )
                else -> Pair(
                    R.color.snackbar_info_background,
                    R.drawable.ic_info
                )
            }

            root.setCardBackgroundColor(ContextCompat.getColor(context, backgroundColor))
            typeIcon.setImageResource(iconResId)
        }

        isVisible = true
        postDelayed({ dismiss() }, duration.toLong())
    }

    fun dismiss() {
        isVisible = false
        dismissCallback?.invoke()
    }

    fun setDuration(duration: Int) {
        this.duration = duration
    }

    fun setOnActionClickListener(listener: () -> Unit) {
        actionClickListener = listener
    }

    fun setOnDismissCallback(callback: () -> Unit) {
        dismissCallback = callback
    }

    companion object {
        const val TYPE_INFO = 0
        const val TYPE_SUCCESS = 1
        const val TYPE_WARNING = 2
        const val TYPE_ERROR = 3
    }
}
