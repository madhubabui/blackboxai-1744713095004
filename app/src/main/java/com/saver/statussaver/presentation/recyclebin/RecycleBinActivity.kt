package com.saver.statussaver.presentation.recyclebin

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.saver.statussaver.R
import com.saver.statussaver.data.model.Status
import com.saver.statussaver.databinding.ActivityRecycleBinBinding
import com.saver.statussaver.presentation.preview.PreviewActivity
import com.saver.statussaver.presentation.status.StatusAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecycleBinActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecycleBinBinding
    private val viewModel: RecycleBinViewModel by viewModels()
    private lateinit var statusAdapter: StatusAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecycleBinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupButtons()
        observeViewModel()

        viewModel.loadRecycleBinStatuses()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        statusAdapter = StatusAdapter(
            onStatusClicked = { status ->
                startActivity(PreviewActivity.createIntent(this, status))
            },
            onStatusLongClicked = { status ->
                viewModel.onStatusSelected(status, true)
            },
            onStatusSelected = { status, isSelected ->
                viewModel.onStatusSelected(status, isSelected)
            }
        )

        binding.recyclerView.adapter = statusAdapter
    }

    private fun setupButtons() {
        binding.restoreButton.setOnClickListener {
            viewModel.restoreSelectedStatuses()
        }

        binding.deleteButton.setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                when (state) {
                    is RecycleBinUiState.Loading -> showLoading()
                    is RecycleBinUiState.Success -> showContent(state)
                    is RecycleBinUiState.Error -> showError(state.message)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.events.collectLatest { event ->
                when (event) {
                    is RecycleBinEvent.ShowMessage -> showMessage(event.message)
                    is RecycleBinEvent.NavigateBack -> finish()
                }
            }
        }
    }

    private fun showLoading() {
        binding.apply {
            progressBar.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            emptyStateLayout.visibility = View.GONE
            bottomAppBar.visibility = View.GONE
        }
    }

    private fun showContent(state: RecycleBinUiState.Success) {
        binding.apply {
            progressBar.visibility = View.GONE
            
            if (state.statuses.isEmpty()) {
                recyclerView.visibility = View.GONE
                emptyStateLayout.visibility = View.VISIBLE
            } else {
                recyclerView.visibility = View.VISIBLE
                emptyStateLayout.visibility = View.GONE
                statusAdapter.submitList(state.statuses)
            }

            // Show/hide bottom bar based on selection
            bottomAppBar.visibility = if (state.isMultiSelectMode) View.VISIBLE else View.GONE
            if (state.isMultiSelectMode) {
                selectionCountText.text = getString(
                    R.string.batch_mode_title,
                    state.selectedCount
                )
            }

            statusAdapter.setMultiSelectMode(state.isMultiSelectMode)
        }
    }

    private fun showError(message: String) {
        binding.apply {
            progressBar.visibility = View.GONE
            recyclerView.visibility = View.GONE
            emptyStateLayout.visibility = View.VISIBLE
            bottomAppBar.visibility = View.GONE
        }
        showMessage(message)
    }

    private fun showMessage(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun showDeleteConfirmationDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_delete_title)
            .setMessage(R.string.dialog_delete_message)
            .setPositiveButton(R.string.dialog_confirm) { _, _ ->
                viewModel.deleteSelectedStatuses()
            }
            .setNegativeButton(R.string.dialog_cancel, null)
            .show()
    }
}
