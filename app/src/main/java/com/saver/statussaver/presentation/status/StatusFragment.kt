package com.saver.statussaver.presentation.status

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.saver.statussaver.data.model.Status
import com.saver.statussaver.data.model.StatusType
import com.saver.statussaver.databinding.FragmentStatusBinding
import com.saver.statussaver.presentation.main.MainUiState
import com.saver.statussaver.presentation.main.MainViewModel
import com.saver.statussaver.presentation.preview.PreviewActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

abstract class StatusFragment : Fragment() {

    private var _binding: FragmentStatusBinding? = null
    private val binding get() = _binding!!

    protected val viewModel: MainViewModel by activityViewModels()
    private lateinit var statusAdapter: StatusAdapter

    abstract val statusType: StatusType

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatusBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        statusAdapter = StatusAdapter(
            onStatusClicked = { status ->
                startActivity(PreviewActivity.createIntent(requireContext(), status))
            },
            onStatusLongClicked = { status ->
                viewModel.onStatusSelected(status, true)
            },
            onStatusSelected = { status, isSelected ->
                viewModel.onStatusSelected(status, isSelected)
            }
        )

        binding.recyclerView.apply {
            adapter = statusAdapter
            layoutManager = GridLayoutManager(requireContext(), 3)
            setHasFixedSize(true)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                when (state) {
                    is MainUiState.Loading -> showLoading()
                    is MainUiState.Success -> {
                        val filteredStatuses = state.statuses.filter { it.type == statusType }
                        if (filteredStatuses.isEmpty()) {
                            showEmptyState()
                        } else {
                            showContent(filteredStatuses, state.isMultiSelectMode)
                        }
                    }
                    is MainUiState.Error -> showError(state.message)
                }
            }
        }
    }

    private fun showLoading() {
        binding.apply {
            progressBar.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            emptyStateLayout.visibility = View.GONE
        }
    }

    private fun showContent(statuses: List<Status>, isMultiSelectMode: Boolean) {
        binding.apply {
            progressBar.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            emptyStateLayout.visibility = View.GONE
        }
        statusAdapter.setMultiSelectMode(isMultiSelectMode)
        statusAdapter.submitList(statuses)
    }

    private fun showEmptyState() {
        binding.apply {
            progressBar.visibility = View.GONE
            recyclerView.visibility = View.GONE
            emptyStateLayout.visibility = View.VISIBLE
        }
    }

    private fun showError(message: String) {
        binding.apply {
            progressBar.visibility = View.GONE
            recyclerView.visibility = View.GONE
            emptyStateLayout.visibility = View.VISIBLE
            emptyStateText.text = message
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
