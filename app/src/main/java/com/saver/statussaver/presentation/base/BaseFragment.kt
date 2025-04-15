package com.saver.statussaver.presentation.base

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.saver.statussaver.R
import com.saver.statussaver.util.PermissionUtils
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

abstract class BaseFragment<VB : ViewBinding, VM : BaseViewModel> : Fragment() {

    private var _binding: VB? = null
    protected val binding get() = _binding!!
    protected abstract val viewModel: VM

    private var permissionCallback: ((Boolean) -> Unit)? = null

    abstract fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB

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
            is UiEvent.NavigateBack -> requireActivity().onBackPressed()
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

    protected fun checkStoragePermission(callback: (Boolean) -> Unit) {
        if (PermissionUtils.hasStoragePermission(requireContext())) {
            callback(true)
        } else {
            permissionCallback = callback
            if (PermissionUtils.shouldShowPermissionRationale(this)) {
                showPermissionRationaleDialog()
            } else {
                requestStoragePermission()
            }
        }
    }

    private fun showPermissionRationaleDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.permission_required)
            .setMessage(R.string.storage_permission_rationale)
            .setPositiveButton(R.string.grant) { _, _ ->
                requestStoragePermission()
            }
            .setNegativeButton(R.string.deny) { _, _ ->
                permissionCallback?.invoke(false)
                permissionCallback = null
            }
            .show()
    }

    private fun requestStoragePermission() {
        PermissionUtils.requestStoragePermission(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PermissionUtils.PERMISSION_REQUEST_CODE) {
            val granted = grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
            permissionCallback?.invoke(granted)
            permissionCallback = null
        }
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
}
