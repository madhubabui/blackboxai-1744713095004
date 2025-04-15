package com.saver.statussaver.presentation.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.saver.statussaver.R
import com.saver.statussaver.databinding.RatingBarViewBinding

class RatingBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = RatingBarViewBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    private var maxRating = DEFAULT_MAX_RATING
    private var currentRating = 0
    private var isEditable = true
    private var onRatingChangeListener: ((Int) -> Unit)? = null

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.RatingBarView,
            defStyleAttr,
            0
        ).apply {
            try {
                maxRating = getInteger(R.styleable.RatingBarView_maxRating, DEFAULT_MAX_RATING)
                currentRating = getInteger(R.styleable.RatingBarView_rating, 0)
                isEditable = getBoolean(R.styleable.RatingBarView_isEditable, true)
                
                val starSize = getDimensionPixelSize(
                    R.styleable.RatingBarView_starSize,
                    resources.getDimensionPixelSize(R.dimen.rating_star_size)
                )
                val starSpacing = getDimensionPixelSize(
                    R.styleable.RatingBarView_starSpacing,
                    resources.getDimensionPixelSize(R.dimen.rating_star_spacing)
                )
                val starColor = getColor(
                    R.styleable.RatingBarView_starColor,
                    ContextCompat.getColor(context, R.color.rating_star)
                )
                val starBorderColor = getColor(
                    R.styleable.RatingBarView_starBorderColor,
                    ContextCompat.getColor(context, R.color.rating_star_border)
                )

                setupRatingBar(
                    maxRating,
                    currentRating,
                    isEditable,
                    starSize,
                    starSpacing,
                    starColor,
                    starBorderColor
                )
            } finally {
                recycle()
            }
        }
    }

    private fun setupRatingBar(
        maxRating: Int,
        currentRating: Int,
        isEditable: Boolean,
        starSize: Int,
        starSpacing: Int,
        starColor: Int,
        starBorderColor: Int
    ) {
        binding.apply {
            // Clear existing stars
            starsContainer.removeAllViews()

            // Add stars
            for (i in 1..maxRating) {
                val star = createStarView(
                    i <= currentRating,
                    starSize,
                    starColor,
                    starBorderColor
                )

                if (isEditable) {
                    star.setOnClickListener {
                        setRating(i)
                    }
                }

                starsContainer.addView(star)

                // Add spacing between stars (except for the last star)
                if (i < maxRating) {
                    val space = Space(context)
                    space.layoutParams = LayoutParams(starSpacing, starSize)
                    starsContainer.addView(space)
                }
            }
        }
    }

    private fun createStarView(
        filled: Boolean,
        size: Int,
        starColor: Int,
        borderColor: Int
    ): ImageView {
        return ImageView(context).apply {
            layoutParams = LayoutParams(size, size)
            setImageResource(
                if (filled) R.drawable.ic_star_filled
                else R.drawable.ic_star_border
            )
            setColorFilter(
                if (filled) starColor
                else borderColor
            )
        }
    }

    fun setRating(rating: Int) {
        if (!isEditable) return

        val newRating = rating.coerceIn(0, maxRating)
        if (currentRating != newRating) {
            currentRating = newRating
            updateStars()
            onRatingChangeListener?.invoke(currentRating)
        }
    }

    fun getRating(): Int = currentRating

    fun setMaxRating(max: Int) {
        if (max < 1) return
        maxRating = max
        currentRating = currentRating.coerceIn(0, maxRating)
        setupRatingBar(
            maxRating,
            currentRating,
            isEditable,
            binding.starsContainer.getChildAt(0).width,
            (binding.starsContainer.getChildAt(1) as? Space)?.width ?: 0,
            (binding.starsContainer.getChildAt(0) as ImageView).colorFilter?.alpha ?: 0,
            (binding.starsContainer.getChildAt(2) as ImageView).colorFilter?.alpha ?: 0
        )
    }

    fun setEditable(editable: Boolean) {
        isEditable = editable
        setupRatingBar(
            maxRating,
            currentRating,
            isEditable,
            binding.starsContainer.getChildAt(0).width,
            (binding.starsContainer.getChildAt(1) as? Space)?.width ?: 0,
            (binding.starsContainer.getChildAt(0) as ImageView).colorFilter?.alpha ?: 0,
            (binding.starsContainer.getChildAt(2) as ImageView).colorFilter?.alpha ?: 0
        )
    }

    private fun updateStars() {
        binding.starsContainer.children.filterIsInstance<ImageView>().forEachIndexed { index, star ->
            val filled = index < currentRating
            star.setImageResource(
                if (filled) R.drawable.ic_star_filled
                else R.drawable.ic_star_border
            )
            star.setColorFilter(
                if (filled) ContextCompat.getColor(context, R.color.rating_star)
                else ContextCompat.getColor(context, R.color.rating_star_border)
            )
        }
    }

    fun setOnRatingChangeListener(listener: (Int) -> Unit) {
        onRatingChangeListener = listener
    }

    companion object {
        private const val DEFAULT_MAX_RATING = 5
    }
}
