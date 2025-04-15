package com.saver.statussaver.presentation.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.saver.statussaver.R
import com.saver.statussaver.databinding.CustomTabLayoutBinding

class CustomTabLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = CustomTabLayoutBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    private var onTabSelectedListener: ((Int) -> Unit)? = null
    private var mediator: TabLayoutMediator? = null

    init {
        setupTabLayout()
    }

    private fun setupTabLayout() {
        binding.tabLayout.apply {
            setSelectedTabIndicator(R.drawable.tab_indicator_selected)
            setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
            tabRippleColor = null
            
            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    tab?.let {
                        onTabSelectedListener?.invoke(it.position)
                        updateTabAppearance(it, true)
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    tab?.let { updateTabAppearance(it, false) }
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                    // Do nothing
                }
            })
        }
    }

    fun setupWithViewPager(
        viewPager: ViewPager2,
        titles: List<String>,
        icons: List<Int>? = null
    ) {
        mediator?.detach()
        
        mediator = TabLayoutMediator(binding.tabLayout, viewPager) { tab, position ->
            tab.text = titles[position]
            icons?.getOrNull(position)?.let { iconRes ->
                tab.setIcon(iconRes)
            }
            updateTabAppearance(tab, position == viewPager.currentItem)
        }.apply { attach() }
    }

    private fun updateTabAppearance(tab: TabLayout.Tab, isSelected: Boolean) {
        tab.view.apply {
            isSelected = isSelected
            alpha = if (isSelected) 1.0f else 0.7f
        }
    }

    fun addTab(title: String, icon: Int? = null) {
        binding.tabLayout.addTab(
            binding.tabLayout.newTab().apply {
                text = title
                icon?.let { setIcon(it) }
            }
        )
    }

    fun removeTab(position: Int) {
        binding.tabLayout.removeTabAt(position)
    }

    fun clearTabs() {
        binding.tabLayout.removeAllTabs()
    }

    fun selectTab(position: Int) {
        binding.tabLayout.getTabAt(position)?.select()
    }

    fun setOnTabSelectedListener(listener: (Int) -> Unit) {
        onTabSelectedListener = listener
    }

    fun setTabTextColors(selectedColor: Int, unselectedColor: Int) {
        binding.tabLayout.setTabTextColors(unselectedColor, selectedColor)
    }

    fun setTabIconTint(selectedColor: Int, unselectedColor: Int) {
        binding.tabLayout.setTabIconTint(
            ContextCompat.getColorStateList(
                context,
                R.color.tab_icon_color_selector
            )
        )
    }

    fun setTabMode(mode: Int) {
        binding.tabLayout.tabMode = mode
    }

    fun setTabGravity(gravity: Int) {
        binding.tabLayout.tabGravity = gravity
    }

    fun setTabIndicatorColor(color: Int) {
        binding.tabLayout.setSelectedTabIndicatorColor(color)
    }

    fun setTabIndicatorHeight(height: Int) {
        binding.tabLayout.setSelectedTabIndicatorHeight(height)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mediator?.detach()
        mediator = null
    }
}
