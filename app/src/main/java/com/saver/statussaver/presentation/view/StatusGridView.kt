package com.saver.statussaver.presentation.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.saver.statussaver.databinding.StatusGridViewBinding
import com.saver.statussaver.presentation.base.BaseAdapter

class StatusGridView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = StatusGridViewBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    private var spanCount = DEFAULT_SPAN_COUNT
    private var itemSpacing = DEFAULT_ITEM_SPACING
    private var onRefreshListener: (() -> Unit)? = null
    private var onSaveClickListener: ((List<Int>) -> Unit)? = null
    private var onShareClickListener: ((List<Int>) -> Unit)? = null
    private var onDeleteClickListener: ((List<Int>) -> Unit)? = null
    private var selectedPositions = mutableSetOf<Int>()

    init {
        setupRecyclerView()
        setupSwipeRefresh()
        setupSelectionActions()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(context, spanCount)
            addItemDecoration(GridSpacingItemDecoration(spanCount, itemSpacing))
            setHasFixedSize(true)
        }

        binding.shimmerRecyclerView.apply {
            layoutManager = GridLayoutManager(context, spanCount)
            addItemDecoration(GridSpacingItemDecoration(spanCount, itemSpacing))
            setHasFixedSize(true)
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            onRefreshListener?.invoke()
        }
    }

    private fun setupSelectionActions() {
        with(binding) {
            saveButton.setOnClickListener {
                onSaveClickListener?.invoke(selectedPositions.toList())
            }

            shareButton.setOnClickListener {
                onShareClickListener?.invoke(selectedPositions.toList())
            }

            deleteButton.setOnClickListener {
                onDeleteClickListener?.invoke(selectedPositions.toList())
            }
        }
    }

    fun setAdapter(adapter: BaseAdapter<*, *>) {
        binding.recyclerView.adapter = adapter
    }

    fun setShimmerAdapter(adapter: BaseAdapter<*, *>) {
        binding.shimmerRecyclerView.adapter = adapter
    }

    fun showLoading(show: Boolean) {
        binding.apply {
            shimmerRecyclerView.isVisible = show
            recyclerView.isVisible = !show
            emptyStateView.isVisible = false
        }
    }

    fun showEmpty(show: Boolean) {
        binding.apply {
            emptyStateView.isVisible = show
            recyclerView.isVisible = !show
            shimmerRecyclerView.isVisible = false
        }
    }

    fun setRefreshing(refreshing: Boolean) {
        binding.swipeRefreshLayout.isRefreshing = refreshing
    }

    fun setOnRefreshListener(listener: () -> Unit) {
        onRefreshListener = listener
    }

    fun setSpanCount(count: Int) {
        spanCount = count
        binding.apply {
            (recyclerView.layoutManager as GridLayoutManager).spanCount = count
            (shimmerRecyclerView.layoutManager as GridLayoutManager).spanCount = count
            recyclerView.invalidateItemDecorations()
            shimmerRecyclerView.invalidateItemDecorations()
        }
    }

    fun setItemSpacing(spacing: Int) {
        itemSpacing = spacing
        binding.apply {
            recyclerView.invalidateItemDecorations()
            shimmerRecyclerView.invalidateItemDecorations()
        }
    }

    fun updateSelection(positions: Set<Int>) {
        selectedPositions = positions.toMutableSet()
        updateSelectionUI()
    }

    private fun updateSelectionUI() {
        binding.apply {
            selectionActionBar.isVisible = selectedPositions.isNotEmpty()
            selectionCountText.text = context.getString(
                com.saver.statussaver.R.string.selection_count,
                selectedPositions.size
            )
        }
    }

    fun setOnSaveClickListener(listener: (List<Int>) -> Unit) {
        onSaveClickListener = listener
    }

    fun setOnShareClickListener(listener: (List<Int>) -> Unit) {
        onShareClickListener = listener
    }

    fun setOnDeleteClickListener(listener: (List<Int>) -> Unit) {
        onDeleteClickListener = listener
    }

    fun clearSelection() {
        selectedPositions.clear()
        updateSelectionUI()
    }

    private class GridSpacingItemDecoration(
        private val spanCount: Int,
        private val spacing: Int
    ) : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(
            outRect: android.graphics.Rect,
            view: android.view.View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view)
            val column = position % spanCount

            outRect.left = spacing - column * spacing / spanCount
            outRect.right = (column + 1) * spacing / spanCount

            if (position >= spanCount) {
                outRect.top = spacing
            }
        }
    }

    companion object {
        private const val DEFAULT_SPAN_COUNT = 2
        private const val DEFAULT_ITEM_SPACING = 8
    }
}
