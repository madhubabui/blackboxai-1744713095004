package com.saver.statussaver.presentation.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.saver.statussaver.databinding.OptionsBottomSheetViewBinding

class OptionsBottomSheetView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = OptionsBottomSheetViewBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    private val adapter = OptionsAdapter { position ->
        options.getOrNull(position)?.let { option ->
            option.onClickListener?.invoke()
            selectedPosition = if (option.selectable) position else -1
            adapter.setSelectedPosition(selectedPosition)
        }
    }

    private val options = mutableListOf<Option>()
    private var selectedPosition = -1

    init {
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@OptionsBottomSheetView.adapter
            setHasFixedSize(true)
        }
    }

    fun setTitle(title: String) {
        binding.titleText.text = title
    }

    fun setSubtitle(subtitle: String?) {
        binding.subtitleText.apply {
            text = subtitle
            isVisible = !subtitle.isNullOrEmpty()
        }
    }

    fun addOption(
        @DrawableRes iconRes: Int,
        text: String,
        selectable: Boolean = false,
        selected: Boolean = false,
        onClickListener: (() -> Unit)? = null
    ) {
        val position = options.size
        options.add(
            Option(
                iconRes = iconRes,
                text = text,
                selectable = selectable,
                onClickListener = onClickListener
            )
        )
        adapter.submitList(options.toList())
        if (selected) {
            selectedPosition = position
            adapter.setSelectedPosition(position)
        }
    }

    fun clearOptions() {
        options.clear()
        adapter.submitList(emptyList())
        selectedPosition = -1
        adapter.setSelectedPosition(-1)
    }

    fun getSelectedPosition(): Int = selectedPosition

    private data class Option(
        @DrawableRes val iconRes: Int,
        val text: String,
        val selectable: Boolean = false,
        val onClickListener: (() -> Unit)? = null
    )

    private class OptionsAdapter(
        private val onItemClick: (Int) -> Unit
    ) : androidx.recyclerview.widget.ListAdapter<Option, OptionsAdapter.ViewHolder>(
        object : androidx.recyclerview.widget.DiffUtil.ItemCallback<Option>() {
            override fun areItemsTheSame(oldItem: Option, newItem: Option): Boolean {
                return oldItem.text == newItem.text
            }

            override fun areContentsTheSame(oldItem: Option, newItem: Option): Boolean {
                return oldItem == newItem
            }
        }
    ) {
        private var selectedPosition = -1

        fun setSelectedPosition(position: Int) {
            val oldPosition = selectedPosition
            selectedPosition = position
            oldPosition.takeIf { it >= 0 }?.let { notifyItemChanged(it) }
            position.takeIf { it >= 0 }?.let { notifyItemChanged(it) }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                OptionsItemViewBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(getItem(position), position == selectedPosition, onItemClick)
        }

        class ViewHolder(
            private val binding: OptionsItemViewBinding
        ) : RecyclerView.ViewHolder(binding.root) {

            fun bind(option: Option, selected: Boolean, onItemClick: (Int) -> Unit) {
                with(binding) {
                    iconImage.setImageResource(option.iconRes)
                    optionText.text = option.text
                    checkIcon.isVisible = option.selectable && selected
                    root.setOnClickListener { onItemClick(adapterPosition) }
                }
            }
        }
    }
}
