package com.saver.statussaver.presentation.view

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.saver.statussaver.R
import com.saver.statussaver.databinding.SegmentedControlViewBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup

class SegmentedControlView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding = SegmentedControlViewBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    private var segments: List<String> = emptyList()
    private var selectedSegment: Int = 0
    private var onSegmentSelectedListener: ((Int) -> Unit)? = null

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.SegmentedControlView,
            defStyleAttr,
            0
        ).apply {
            try {
                // Get segments array from resources if provided
                getResourceId(R.styleable.SegmentedControlView_segments, 0).let { resId ->
                    if (resId != 0) {
                        segments = resources.getStringArray(resId).toList()
                    }
                }
                selectedSegment = getInteger(R.styleable.SegmentedControlView_selectedSegment, 0)
                
                val segmentHeight = getDimensionPixelSize(
                    R.styleable.SegmentedControlView_segmentHeight,
                    resources.getDimensionPixelSize(R.dimen.segment_height)
                )
                val segmentColor = getColor(
                    R.styleable.SegmentedControlView_segmentColor,
                    ContextCompat.getColor(context, R.color.segment_color)
                )
                val selectedTextColor = getColor(
                    R.styleable.SegmentedControlView_selectedTextColor,
                    ContextCompat.getColor(context, R.color.segment_selected_text)
                )
                val unselectedTextColor = getColor(
                    R.styleable.SegmentedControlView_unselectedTextColor,
                    ContextCompat.getColor(context, R.color.segment_unselected_text)
                )
                val textAppearance = getResourceId(
                    R.styleable.SegmentedControlView_segmentTextAppearance,
                    R.style.TextAppearance_MaterialComponents_Button
                )
                val cornerRadius = getDimensionPixelSize(
                    R.styleable.SegmentedControlView_segmentCornerRadius,
                    resources.getDimensionPixelSize(R.dimen.segment_corner_radius)
                )
                val strokeWidth = getDimensionPixelSize(
                    R.styleable.SegmentedControlView_segmentStrokeWidth,
                    resources.getDimensionPixelSize(R.dimen.segment_stroke_width)
                )
                val strokeColor = getColor(
                    R.styleable.SegmentedControlView_segmentStrokeColor,
                    ContextCompat.getColor(context, R.color.segment_stroke)
                )
                val margin = getDimensionPixelSize(
                    R.styleable.SegmentedControlView_segmentMargin,
                    resources.getDimensionPixelSize(R.dimen.segment_margin)
                )
                val padding = getDimensionPixelSize(
                    R.styleable.SegmentedControlView_segmentPadding,
                    resources.getDimensionPixelSize(R.dimen.segment_padding)
                )

                setupSegments(
                    segmentHeight,
                    segmentColor,
                    selectedTextColor,
                    unselectedTextColor,
                    textAppearance,
                    cornerRadius,
                    strokeWidth,
                    strokeColor,
                    margin,
                    padding
                )
            } finally {
                recycle()
            }
        }
    }

    private fun setupSegments(
        height: Int,
        segmentColor: Int,
        selectedTextColor: Int,
        unselectedTextColor: Int,
        textAppearance: Int,
        cornerRadius: Int,
        strokeWidth: Int,
        strokeColor: Int,
        margin: Int,
        padding: Int
    ) {
        binding.toggleGroup.apply {
            removeAllViews()
            segments.forEachIndexed { index, text ->
                addView(createSegmentButton(
                    text,
                    index,
                    height,
                    segmentColor,
                    selectedTextColor,
                    unselectedTextColor,
                    textAppearance,
                    cornerRadius,
                    strokeWidth,
                    strokeColor,
                    margin,
                    padding
                ))
            }
            check(getSegmentId(selectedSegment))
            addOnButtonCheckedListener { _, checkedId, isChecked ->
                if (isChecked) {
                    val position = getSegmentPosition(checkedId)
                    onSegmentSelectedListener?.invoke(position)
                }
            }
        }
    }

    private fun createSegmentButton(
        text: String,
        position: Int,
        height: Int,
        segmentColor: Int,
        selectedTextColor: Int,
        unselectedTextColor: Int,
        textAppearance: Int,
        cornerRadius: Int,
        strokeWidth: Int,
        strokeColor: Int,
        margin: Int,
        padding: Int
    ): MaterialButton {
        return MaterialButton(context, null, R.attr.materialButtonOutlinedStyle).apply {
            id = getSegmentId(position)
            this.text = text
            layoutParams = MarginLayoutParams(
                LayoutParams.WRAP_CONTENT,
                height
            ).apply {
                setMargins(margin, margin, margin, margin)
            }
            setPadding(padding, 0, padding, 0)
            setTextAppearance(textAppearance)
            setTextColor(ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_checked),
                    intArrayOf()
                ),
                intArrayOf(
                    selectedTextColor,
                    unselectedTextColor
                )
            ))
            backgroundTintList = ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_checked),
                    intArrayOf()
                ),
                intArrayOf(
                    segmentColor,
                    android.graphics.Color.TRANSPARENT
                )
            )
            cornerRadius = this@apply.cornerRadius
            strokeWidth = this@apply.strokeWidth
            strokeColor = ColorStateList.valueOf(strokeColor)
        }
    }

    private fun getSegmentId(position: Int): Int = View.generateViewId()

    private fun getSegmentPosition(id: Int): Int {
        return binding.toggleGroup.indexOfChild(
            binding.toggleGroup.findViewById(id)
        )
    }

    fun setSegments(segments: List<String>) {
        this.segments = segments
        setupSegments(
            resources.getDimensionPixelSize(R.dimen.segment_height),
            ContextCompat.getColor(context, R.color.segment_color),
            ContextCompat.getColor(context, R.color.segment_selected_text),
            ContextCompat.getColor(context, R.color.segment_unselected_text),
            R.style.TextAppearance_MaterialComponents_Button,
            resources.getDimensionPixelSize(R.dimen.segment_corner_radius),
            resources.getDimensionPixelSize(R.dimen.segment_stroke_width),
            ContextCompat.getColor(context, R.color.segment_stroke),
            resources.getDimensionPixelSize(R.dimen.segment_margin),
            resources.getDimensionPixelSize(R.dimen.segment_padding)
        )
    }

    fun setSelectedSegment(position: Int) {
        if (position in segments.indices) {
            binding.toggleGroup.check(getSegmentId(position))
        }
    }

    fun setOnSegmentSelectedListener(listener: (Int) -> Unit) {
        onSegmentSelectedListener = listener
    }
}
