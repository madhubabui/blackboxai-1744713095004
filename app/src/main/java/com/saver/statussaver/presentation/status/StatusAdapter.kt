package com.saver.statussaver.presentation.status

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.saver.statussaver.data.model.Status
import com.saver.statussaver.data.model.StatusType
import com.saver.statussaver.databinding.ItemStatusBinding
import java.util.concurrent.TimeUnit

class StatusAdapter(
    private val onStatusClicked: (Status) -> Unit,
    private val onStatusLongClicked: (Status) -> Unit,
    private val onStatusSelected: (Status, Boolean) -> Unit
) : ListAdapter<Status, StatusAdapter.StatusViewHolder>(StatusDiffCallback()) {

    private var isMultiSelectMode = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusViewHolder {
        val binding = ItemStatusBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StatusViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StatusViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun setMultiSelectMode(enabled: Boolean) {
        if (isMultiSelectMode != enabled) {
            isMultiSelectMode = enabled
            notifyDataSetChanged()
        }
    }

    inner class StatusViewHolder(
        private val binding: ItemStatusBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val status = getItem(position)
                    if (isMultiSelectMode) {
                        toggleSelection(status)
                    } else {
                        onStatusClicked(status)
                    }
                }
            }

            binding.root.setOnLongClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val status = getItem(position)
                    if (!isMultiSelectMode) {
                        onStatusLongClicked(status)
                        toggleSelection(status)
                        return@setOnLongClickListener true
                    }
                }
                false
            }

            binding.selectionCheckbox.setOnCheckedChangeListener { _, isChecked ->
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onStatusSelected(getItem(position), isChecked)
                }
            }
        }

        fun bind(status: Status) {
            // Load thumbnail
            Glide.with(binding.thumbnailImage)
                .load(status.uri)
                .centerCrop()
                .into(binding.thumbnailImage)

            // Show/hide video specific UI
            binding.playIcon.visibility = if (status.isVideo) View.VISIBLE else View.GONE
            binding.durationText.visibility = if (status.isVideo) View.VISIBLE else View.GONE

            // Set video duration if available
            if (status.isVideo) {
                binding.durationText.text = formatDuration(status.size) // This is a placeholder, actual duration should come from MediaMetadataRetriever
            }

            // Handle selection state
            binding.selectionCheckbox.visibility = if (isMultiSelectMode) View.VISIBLE else View.GONE
            binding.selectionOverlay.visibility = if (binding.selectionCheckbox.isChecked) View.VISIBLE else View.GONE
        }

        private fun toggleSelection(status: Status) {
            binding.selectionCheckbox.isChecked = !binding.selectionCheckbox.isChecked
            binding.selectionOverlay.visibility = if (binding.selectionCheckbox.isChecked) View.VISIBLE else View.GONE
            onStatusSelected(status, binding.selectionCheckbox.isChecked)
        }

        private fun formatDuration(milliseconds: Long): String {
            val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
            val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                    TimeUnit.MINUTES.toSeconds(minutes)
            return String.format("%02d:%02d", minutes, seconds)
        }
    }

    private class StatusDiffCallback : DiffUtil.ItemCallback<Status>() {
        override fun areItemsTheSame(oldItem: Status, newItem: Status): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Status, newItem: Status): Boolean {
            return oldItem == newItem
        }
    }
}
