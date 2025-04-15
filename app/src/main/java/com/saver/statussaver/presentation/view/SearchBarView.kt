package com.saver.statussaver.presentation.view

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.saver.statussaver.R
import com.saver.statussaver.databinding.SearchBarViewBinding

class SearchBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = SearchBarViewBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    private var onSearchTextChanged: ((String) -> Unit)? = null
    private var onSearchSubmitted: ((String) -> Unit)? = null
    private var onFilterClick: (() -> Unit)? = null
    private var onClearClick: (() -> Unit)? = null

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.SearchBarView,
            defStyleAttr,
            0
        ).apply {
            try {
                val hint = getString(R.styleable.SearchBarView_searchHint)
                    ?: context.getString(R.string.search_hint)
                val showFilter = getBoolean(R.styleable.SearchBarView_showFilter, true)
                val backgroundColor = getColor(
                    R.styleable.SearchBarView_searchBackgroundColor,
                    ContextCompat.getColor(context, R.color.search_background)
                )
                val textColor = getColor(
                    R.styleable.SearchBarView_searchTextColor,
                    ContextCompat.getColor(context, R.color.search_text)
                )
                val iconTint = getColor(
                    R.styleable.SearchBarView_searchIconTint,
                    ContextCompat.getColor(context, R.color.search_icon)
                )

                setupSearchBar(
                    hint,
                    showFilter,
                    backgroundColor,
                    textColor,
                    iconTint
                )
            } finally {
                recycle()
            }
        }
    }

    private fun setupSearchBar(
        hint: String,
        showFilter: Boolean,
        backgroundColor: Int,
        textColor: Int,
        iconTint: Int
    ) {
        binding.apply {
            // Setup background
            root.setCardBackgroundColor(backgroundColor)

            // Setup search field
            searchInput.apply {
                setHintTextColor(textColor.withAlpha(0.6f))
                setTextColor(textColor)
                this.hint = hint
            }

            // Setup icons
            searchIcon.setColorFilter(iconTint)
            clearButton.setColorFilter(iconTint)
            filterButton.setColorFilter(iconTint)

            // Setup filter button
            filterButton.isVisible = showFilter

            // Setup click listeners
            clearButton.setOnClickListener {
                searchInput.text?.clear()
                onClearClick?.invoke()
            }

            filterButton.setOnClickListener {
                onFilterClick?.invoke()
            }

            // Setup text watcher
            searchInput.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    val text = s?.toString() ?: ""
                    clearButton.isVisible = text.isNotEmpty()
                    onSearchTextChanged?.invoke(text)
                }
            })

            // Setup keyboard action listener
            searchInput.setOnEditorActionListener { _, _, _ ->
                val text = searchInput.text?.toString() ?: ""
                onSearchSubmitted?.invoke(text)
                true
            }
        }
    }

    fun setSearchText(text: String) {
        binding.searchInput.setText(text)
    }

    fun getSearchText(): String = binding.searchInput.text?.toString() ?: ""

    fun setHint(hint: String) {
        binding.searchInput.hint = hint
    }

    fun showFilter(show: Boolean) {
        binding.filterButton.isVisible = show
    }

    fun setBackgroundColor(color: Int) {
        binding.root.setCardBackgroundColor(color)
    }

    fun setTextColor(color: Int) {
        binding.searchInput.apply {
            setTextColor(color)
            setHintTextColor(color.withAlpha(0.6f))
        }
    }

    fun setIconTint(color: Int) {
        binding.apply {
            searchIcon.setColorFilter(color)
            clearButton.setColorFilter(color)
            filterButton.setColorFilter(color)
        }
    }

    fun setOnSearchTextChangedListener(listener: (String) -> Unit) {
        onSearchTextChanged = listener
    }

    fun setOnSearchSubmittedListener(listener: (String) -> Unit) {
        onSearchSubmitted = listener
    }

    fun setOnFilterClickListener(listener: () -> Unit) {
        onFilterClick = listener
    }

    fun setOnClearClickListener(listener: () -> Unit) {
        onClearClick = listener
    }

    private fun Int.withAlpha(alpha: Float): Int {
        return (alpha * 255).toInt() shl 24 or (this and 0x00FFFFFF)
    }
}
