package com.saver.statussaver.presentation.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseAdapter<T : Any, VB : ViewBinding>(
    diffCallback: DiffUtil.ItemCallback<T>
) : ListAdapter<T, BaseAdapter.BaseViewHolder<T, VB>>(diffCallback) {

    private var isMultiSelectMode = false
    private val selectedItems = mutableSetOf<T>()

    var onItemClick: ((T) -> Unit)? = null
    var onItemLongClick: ((T) -> Unit)? = null
    var onSelectionChanged: ((Set<T>) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<T, VB> {
        val binding = createBinding(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BaseViewHolder(
            binding = binding,
            onBind = { position, item -> bindItem(binding, item, position) },
            onItemClick = { position ->
                val item = getItem(position)
                if (isMultiSelectMode) {
                    toggleSelection(item)
                } else {
                    onItemClick?.invoke(item)
                }
            },
            onItemLongClick = { position ->
                val item = getItem(position)
                if (!isMultiSelectMode) {
                    setMultiSelectMode(true)
                    toggleSelection(item)
                }
                onItemLongClick?.invoke(item)
            }
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder<T, VB>, position: Int) {
        val item = getItem(position)
        holder.bind(position, item)
        holder.itemView.isSelected = selectedItems.contains(item)
    }

    abstract fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        attachToParent: Boolean
    ): VB

    abstract fun bindItem(binding: VB, item: T, position: Int)

    fun setMultiSelectMode(enabled: Boolean) {
        if (isMultiSelectMode != enabled) {
            isMultiSelectMode = enabled
            if (!enabled) {
                clearSelection()
            }
            notifyDataSetChanged()
        }
    }

    fun isInMultiSelectMode() = isMultiSelectMode

    fun getSelectedItems(): Set<T> = selectedItems.toSet()

    fun toggleSelection(item: T) {
        if (selectedItems.contains(item)) {
            selectedItems.remove(item)
            if (selectedItems.isEmpty()) {
                setMultiSelectMode(false)
            }
        } else {
            selectedItems.add(item)
        }
        notifyItemChanged(currentList.indexOf(item))
        onSelectionChanged?.invoke(selectedItems)
    }

    fun selectAll() {
        selectedItems.clear()
        selectedItems.addAll(currentList)
        notifyDataSetChanged()
        onSelectionChanged?.invoke(selectedItems)
    }

    fun clearSelection() {
        selectedItems.clear()
        notifyDataSetChanged()
        onSelectionChanged?.invoke(selectedItems)
    }

    fun isSelected(item: T) = selectedItems.contains(item)

    class BaseViewHolder<T, VB : ViewBinding>(
        private val binding: VB,
        private val onBind: (Int, T) -> Unit,
        onItemClick: (Int) -> Unit,
        onItemLongClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    onItemClick(bindingAdapterPosition)
                }
            }
            itemView.setOnLongClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    onItemLongClick(bindingAdapterPosition)
                }
                true
            }
        }

        fun bind(position: Int, item: T) {
            onBind(position, item)
        }
    }
}

abstract class BaseDiffCallback<T : Any> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }
}
