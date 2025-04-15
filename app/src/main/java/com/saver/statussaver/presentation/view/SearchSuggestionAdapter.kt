package com.saver.statussaver.presentation.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.saver.statussaver.databinding.SearchSuggestionItemViewBinding

class SearchSuggestionAdapter(
    private val onSuggestionClick: (String) -> Unit,
    private val onRemoveClick: (String) -> Unit
) : ListAdapter<String, SearchSuggestionAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            SearchSuggestionItemViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: SearchSuggestionItemViewBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onSuggestionClick(getItem(adapterPosition))
                }
            }

            binding.removeButton.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onRemoveClick(getItem(adapterPosition))
                }
            }
        }

        fun bind(suggestion: String) {
            binding.suggestionText.text = suggestion
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }
        }
    }
}
