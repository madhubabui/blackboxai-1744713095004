package com.saver.statussaver.presentation.tutorial

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.saver.statussaver.databinding.ItemTutorialPageBinding

class TutorialPagerAdapter(
    private val pages: List<TutorialPage>
) : RecyclerView.Adapter<TutorialPagerAdapter.TutorialPageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TutorialPageViewHolder {
        val binding = ItemTutorialPageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TutorialPageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TutorialPageViewHolder, position: Int) {
        holder.bind(pages[position])
    }

    override fun getItemCount(): Int = pages.size

    class TutorialPageViewHolder(
        private val binding: ItemTutorialPageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(page: TutorialPage) {
            binding.apply {
                tutorialImage.setImageResource(page.imageResId)
                titleText.text = page.title
                descriptionText.text = page.description
            }
        }
    }
}
