package com.saver.statussaver.presentation.base

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
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

abstract class BaseActivity<VB : ViewBinding, VM : BaseViewModel> : AppCompatActivity() {

    protected abstract val binding: VB
    protected abstract val viewModel: VM

    private var permissionCallback: ((Boolean) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupActionBar()
        initViews()
        observeEvents()
    }

    protected open fun setupActionBar() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
    }

    protected open fun initViews() {
        // Override in child classes if needed
    }

    private fun observeEvents() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
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
            is UiEvent.NavigateBack -> onBackPressed()
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
        if (PermissionUtils.hasStoragePermission(this)) {
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
        MaterialAlertDialogBuilder(this)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    protected fun <T> SharedFlow<T>.collectWithLifecycle(action: suspend (T) -> Unit) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                collect { action(it) }
            }
        }
    }
}
