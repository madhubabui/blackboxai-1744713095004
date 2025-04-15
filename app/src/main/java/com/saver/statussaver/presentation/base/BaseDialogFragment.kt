package com.saver.statussaver.presentation.base

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.saver.statussaver.R
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

abstract class BaseDialogFragment<VB : ViewBinding, VM : BaseViewModel> : DialogFragment() {

    private var _binding: VB? = null
    protected val binding get() = _binding!!
    protected abstract val viewModel: VM

    abstract fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.Theme_Saver_Dialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = getViewBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observeEvents()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext(), theme)
            .setView(onCreateView(layoutInflater, null, savedInstanceState))
            .create()
            .apply {
                window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            }
    }

    protected open fun initViews() {
        // Override in child classes if needed
    }

    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { event ->
                    handleEvent(event)
                }
            }
        }
    }

    protected open fun handleEvent(event: UiEvent) {
        when (event) {
            is UiEvent.ShowMessage -> showMessage(event.message)
            is UiEvent.Navigate -> handleNavigation(event.route)
            is UiEvent.NavigateBack -> dismiss()
            is UiEvent.Refresh -> refreshContent()
        }
    }

    protected open fun handleNavigation(route: String) {
        // Override in child classes if needed
    }

    protected open fun refreshContent() {
        // Override in child classes if needed
    }

    protected fun showMessage(message: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(message)
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }

    protected fun <T> SharedFlow<T>.collectWithLifecycle(action: suspend (T) -> Unit) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                collect { action(it) }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    protected fun setDialogSize(widthPercent: Float = 0.9f, heightPercent: Float? = null) {
        dialog?.window?.let { window ->
            val displayMetrics = resources.displayMetrics
            val width = (displayMetrics.widthPixels * widthPercent).toInt()
            val params = window.attributes.apply {
                this.width = width
                heightPercent?.let {
                    this.height = (displayMetrics.heightPixels * it).toInt()
                }
            }
            window.attributes = params
        }
    }

    protected fun enableFullScreen() {
        dialog?.window?.let { window ->
            window.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    protected fun enableDraggable() {
        dialog?.window?.let { window ->
            window.attributes.apply {
                flags = flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()
            }
        }
    }

    protected fun setWindowAnimation(animationStyle: Int) {
        dialog?.window?.attributes?.windowAnimations = animationStyle
    }
}
