package com.saver.statussaver.presentation.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.saver.statussaver.presentation.status.ImageStatusFragment
import com.saver.statussaver.presentation.status.VideoStatusFragment

class StatusPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ImageStatusFragment()
            1 -> VideoStatusFragment()
            else -> throw IllegalArgumentException("Invalid position $position")
        }
    }
}
