package com.saver.statussaver.presentation.base

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.saver.statussaver.R
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

abstract class BaseBottomSheetDialogFragment<VB : ViewBinding, VM : BaseViewModel> : BottomSheetDialogFragment() {

    private var _binding: VB? = null
    protected val binding get() = _binding!!
    protected abstract val viewModel: VM

    private var behavior: BottomSheetBehavior<FrameLayout>? = null

    abstract fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_Saver_BottomSheetDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = getViewBinding(inflater, container)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setOnShowListener {
                val bottomSheet = (it as BottomSheetDialog)
                    .findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
                bottomSheet?.let { sheet ->
                    behavior = BottomSheetBehavior.from(sheet)
                    setupBottomSheet(behavior)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observeEvents()
    }

    protected open fun setupBottomSheet(behavior: BottomSheetBehavior<FrameLayout>?) {
        behavior?.apply {
            state = BottomSheetBehavior.STATE_EXPANDED
            skipCollapsed = true
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
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    protected fun <T> SharedFlow<T>.collectWithLifecycle(action: suspend (T) -> Unit) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                collect { action(it) }
            }
        }
    }

    protected fun setBottomSheetState(state: Int) {
        behavior?.state = state
    }

    protected fun enableDragging(enable: Boolean) {
        behavior?.isDraggable = enable
    }

    protected fun setBottomSheetCallback(callback: BottomSheetBehavior.BottomSheetCallback) {
        behavior?.addBottomSheetCallback(callback)
    }

    protected fun setPeekHeight(heightDp: Int) {
        val heightPx = (resources.displayMetrics.density * heightDp).toInt()
        behavior?.setPeekHeight(heightPx, true)
    }

    protected fun setMaxHeight(heightDp: Int) {
        val heightPx = (resources.displayMetrics.density * heightDp).toInt()
        dialog?.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)?.apply {
            layoutParams = layoutParams.apply {
                height = heightPx
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        behavior = null
    }
}
