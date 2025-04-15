package com.saver.statussaver.presentation.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.saver.statussaver.presentation.sample.CustomViewsSampleActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.saver.statussaver.R
import com.saver.statussaver.databinding.ActivityMainBinding
import com.saver.statussaver.presentation.recyclebin.RecycleBinActivity
import com.saver.statussaver.presentation.settings.SettingsActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var pagerAdapter: StatusPagerAdapter

    private val storagePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.loadStatuses()
        } else {
            showPermissionDeniedDialog()
        }
    }

    private val manageStorageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (Environment.isExternalStorageManager()) {
            viewModel.loadStatuses()
        } else {
            showPermissionDeniedDialog()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupViewPager()
        setupFab()
        observeViewModel()
        checkPermissions()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
    }

    private fun setupViewPager() {
        pagerAdapter = StatusPagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.tab_images)
                1 -> getString(R.string.tab_videos)
                else -> ""
            }
        }.attach()

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                viewModel.onTabSelected(position)
            }
        })
    }

    private fun setupFab() {
        binding.fabSave.setOnClickListener {
            viewModel.saveSelectedStatuses()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                updateUiState(state)
            }
        }

        lifecycleScope.launch {
            viewModel.events.collectLatest { event ->
                handleEvent(event)
            }
        }
    }

    private fun updateUiState(state: MainUiState) {
        when (state) {
            is MainUiState.Loading -> showLoading()
            is MainUiState.Success -> showContent()
            is MainUiState.Error -> showError(state.message)
        }

        binding.fabSave.visibility = if (state.isMultiSelectMode) View.VISIBLE else View.GONE
    }

    private fun handleEvent(event: MainEvent) {
        when (event) {
            is MainEvent.ShowMessage -> showSnackbar(event.message)
            is MainEvent.NavigateToSettings -> startActivity(
                Intent(this, SettingsActivity::class.java)
            )
            is MainEvent.NavigateToRecycleBin -> startActivity(
                Intent(this, RecycleBinActivity::class.java)
            )
        }
    }

    private fun showLoading() {
        binding.stateLayout.visibility = View.VISIBLE
        binding.progressBar.visibility = View.VISIBLE
        binding.errorLayout.visibility = View.GONE
    }

    private fun showContent() {
        binding.stateLayout.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.errorLayout.visibility = View.GONE
    }

    private fun showError(message: String) {
        binding.stateLayout.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
        binding.errorLayout.visibility = View.VISIBLE
        binding.errorText.text = message
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                requestManageStoragePermission()
            } else {
                viewModel.loadStatuses()
            }
        } else {
            when {
                isStoragePermissionGranted() -> viewModel.loadStatuses()
                shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) -> {
                    showPermissionRationaleDialog()
                }
                else -> requestStoragePermission()
            }
        }
    }

    private fun isStoragePermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestStoragePermission() {
        storagePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    private fun requestManageStoragePermission() {
        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
            data = Uri.parse("package:$packageName")
        }
        manageStorageLauncher.launch(intent)
    }

    private fun showPermissionRationaleDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.permission_required)
            .setMessage(R.string.permission_rationale)
            .setPositiveButton(R.string.dialog_confirm) { _, _ -> requestStoragePermission() }
            .setNegativeButton(R.string.dialog_cancel) { _, _ -> finish() }
            .show()
    }

    private fun showPermissionDeniedDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.permission_required)
            .setMessage(R.string.permission_denied)
            .setPositiveButton(R.string.dialog_confirm) { _, _ ->
                startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                })
            }
            .setNegativeButton(R.string.dialog_cancel) { _, _ -> finish() }
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                viewModel.onSettingsClicked()
                true
            }
            R.id.action_recycle_bin -> {
                viewModel.onRecycleBinClicked()
                true
            }
            R.id.action_custom_views -> {
                startActivity(Intent(this, CustomViewsSampleActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
