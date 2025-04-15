package com.saver.statussaver.presentation.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.view.children
import com.saver.statussaver.R
import com.saver.statussaver.databinding.ChipGroupViewBinding

class ChipGroupView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = ChipGroupViewBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    private var chips: List<ChipData> = emptyList()
    private var onChipClickListener: ((Int) -> Unit)? = null
    private var onChipCloseListener: ((Int) -> Unit)? = null
    private var singleSelection = false
    private var selectedChipIndex = -1

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.ChipGroupView,
            defStyleAttr,
            0
        ).apply {
            try {
                singleSelection = getBoolean(R.styleable.ChipGroupView_singleSelection, false)
                val chipSpacing = getDimensionPixelSize(
                    R.styleable.ChipGroupView_chipSpacing,
                    resources.getDimensionPixelSize(R.dimen.chip_spacing)
                )
                val chipSpacingHorizontal = getDimensionPixelSize(
                    R.styleable.ChipGroupView_chipSpacingHorizontal,
                    chipSpacing
                )
                val chipSpacingVertical = getDimensionPixelSize(
                    R.styleable.ChipGroupView_chipSpacingVertical,
                    chipSpacing
                )

                setupChipGroup(chipSpacingHorizontal, chipSpacingVertical)
            } finally {
                recycle()
            }
        }
    }

    private fun setupChipGroup(
        horizontalSpacing: Int,
        verticalSpacing: Int
    ) {
        binding.chipFlowLayout.apply {
            setHorizontalSpacing(horizontalSpacing)
            setVerticalSpacing(verticalSpacing)
        }
    }

    fun setChips(chips: List<ChipData>) {
        this.chips = chips
        binding.chipFlowLayout.removeAllViews()
        
        chips.forEachIndexed { index, chipData ->
            val chip = createChip(index, chipData)
            binding.chipFlowLayout.addView(chip)
        }
    }

    private fun createChip(index: Int, data: ChipData): ChipView {
        return ChipView(context).apply {
            setText(data.text)
            data.icon?.let { setIcon(it) }
            setCloseable(data.isCloseable)
            isCheckable = data.isCheckable
            isChecked = data.isChecked
            
            setOnChipClickListener {
                if (singleSelection) {
                    handleSingleSelection(index)
                }
                onChipClickListener?.invoke(index)
            }
            
            setOnCloseListener {
                onChipCloseListener?.invoke(index)
            }
        }
    }

    private fun handleSingleSelection(selectedIndex: Int) {
        if (selectedChipIndex != selectedIndex) {
            // Uncheck previous selection
            if (selectedChipIndex >= 0) {
                (binding.chipFlowLayout.getChildAt(selectedChipIndex) as? ChipView)?.isChecked = false
            }
            // Check new selection
            (binding.chipFlowLayout.getChildAt(selectedIndex) as? ChipView)?.isChecked = true
            selectedChipIndex = selectedIndex
        }
    }

    fun selectChip(index: Int) {
        if (index in chips.indices) {
            if (singleSelection) {
                handleSingleSelection(index)
            } else {
                (binding.chipFlowLayout.getChildAt(index) as? ChipView)?.isChecked = true
            }
        }
    }

    fun unselectChip(index: Int) {
        if (index in chips.indices) {
            (binding.chipFlowLayout.getChildAt(index) as? ChipView)?.isChecked = false
            if (singleSelection && selectedChipIndex == index) {
                selectedChipIndex = -1
            }
        }
    }

    fun getSelectedChips(): List<Int> {
        return binding.chipFlowLayout.children.toList()
            .mapIndexedNotNull { index, view ->
                if ((view as? ChipView)?.isChecked == true) index else null
            }
    }

    fun setOnChipClickListener(listener: (Int) -> Unit) {
        onChipClickListener = listener
    }

    fun setOnChipCloseListener(listener: (Int) -> Unit) {
        onChipCloseListener = listener
    }

    data class ChipData(
        val text: String,
        val icon: Int? = null,
        val isCloseable: Boolean = false,
        val isCheckable: Boolean = false,
        val isChecked: Boolean = false
    )
}
