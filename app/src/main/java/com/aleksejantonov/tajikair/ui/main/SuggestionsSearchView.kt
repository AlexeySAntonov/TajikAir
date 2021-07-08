package com.aleksejantonov.tajikair.ui.main

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Typeface
import android.text.InputType
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aleksejantonov.tajikair.R
import com.aleksejantonov.tajikair.api.entity.City
import com.aleksejantonov.tajikair.databinding.ItemSuggestionBinding
import com.aleksejantonov.tajikair.ui.base.LayoutHelper
import com.aleksejantonov.tajikair.util.*

class SuggestionsSearchView(context: Context, attributeSet: AttributeSet) : FrameLayout(context, attributeSet) {

  enum class Type {
    DEPARTURE,
    DESTINATION,
  }

  private var labelTextView: TextView? = null
  private var searchEditText: EditText? = null
  private var clearImageView: ImageView? = null
  private var suggestionsRecyclerView: RecyclerView? = null
  private val suggestionsAdapter by lazy { SuggestionsAdapter { onSuggestionClickedInternal(it) } }

  private var animatorSet: AnimatorSet? = null
  private var skipNextTextChangedIteration: Boolean = false

  private var queryChangedListener: ((query: String) -> Unit)? = null
  private var suggestionsClickedListener: ((suggestion: City) -> Unit)? = null
  private var clearedListener: (() -> Unit)? = null

  init {
    layoutParams = LayoutHelper.getFrameParams(
      context = context,
      width = LayoutHelper.MATCH_PARENT,
      height = LayoutHelper.WRAP_CONTENT,
    )
    isClickable = true
    isFocusable = true
    clipChildren = false
    clipToPadding = false
    setOnClickListener { searchEditText?.showKeyboard() }
    setupLabelTextView()
    setupSearchEditText()
    setupClearImageView()
    setupSuggestionsRecyclerView()
  }

  override fun onDetachedFromWindow() {
    animatorSet?.cancel()
    animatorSet = null
    suggestionsRecyclerView = null
    super.onDetachedFromWindow()
  }

  // Just too lazy to use xml style attrs
  fun setType(type: Type) {
    when (type) {
      Type.DEPARTURE -> {
        labelTextView?.text = resources.getString(R.string.from_label)
        searchEditText?.setHint(R.string.departure_search_hint)
      }
      Type.DESTINATION -> {
        labelTextView?.text = resources.getString(R.string.to_label)
        searchEditText?.setHint(R.string.destination_search_hint)
      }
    }
  }

  fun onQueryChanged(listener: (query: String) -> Unit) {
    queryChangedListener = listener
  }

  fun onSuggestionClicked(listener: (suggestion: City) -> Unit) {
    suggestionsClickedListener = listener
  }

  fun onCleared(listener: () -> Unit) {
    clearedListener = listener
  }

  fun setSearchText(text: String) {
    skipNextTextChangedIteration = true
    searchEditText?.setText(text)
    searchEditText?.setSelection(searchEditText?.text?.length ?: 0)
  }

  fun swapSuggestions(cities: List<City>) {
    suggestionsAdapter.setItems(cities)
  }

  private fun setupLabelTextView() {
    labelTextView = TextView(context).apply {
      layoutParams = LayoutHelper.getFrameParams(
        context = context,
        width = LayoutHelper.MATCH_PARENT,
        height = LABEL_HEIGHT,
        gravity = Gravity.START or Gravity.TOP,
        leftMargin = LABEL_MARGIN_START_END,
        rightMargin = LABEL_MARGIN_START_END,
        topMargin = LABEL_MARGIN_TOP,
      )
      ellipsize = TextUtils.TruncateAt.END
      maxLines = 1
      textSize = 14f
      textColor(R.color.white)
      typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
    }
    labelTextView?.let { addView(it) }
  }

  private fun setupSearchEditText() {
    searchEditText = EditText(context).apply {
      layoutParams = LayoutHelper.getFrameParams(
        context = context,
        width = LayoutHelper.MATCH_PARENT,
        height = SEARCH_HEIGHT,
        gravity = Gravity.START or Gravity.TOP,
        leftMargin = LABEL_MARGIN_START_END,
        rightMargin = LABEL_MARGIN_START_END,
        topMargin = SEARCH_MARGIN_TOP_SUMMARY,
      )
      setPaddings(left = dpToPx(SEARCH_PADDING_START), right = dpToPx(SEARCH_PADDING_END))
      hintTextColor(R.color.appGrey)
      textColor(R.color.appGrey)
      setBackgroundResource(R.drawable.background_search)
      maxLines = 1
      inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS
      doAfterTextChanged { newQuery ->
        if (skipNextTextChangedIteration) {
          skipNextTextChangedIteration = false
        } else {
          newQuery?.let { queryChangedListener?.invoke(it.toString()) }
        }
        if (newQuery.isNullOrBlank()) animateClearHide() else animateClearShow()
      }
    }
    searchEditText?.let { addView(it) }
  }

  private fun setupClearImageView() {
    clearImageView = ImageView(context).apply {
      layoutParams = LayoutHelper.getFrameParams(
        context = context,
        width = CLEAR_IMAGE_DIMEN,
        height = CLEAR_IMAGE_DIMEN,
        rightMargin = CLEAR_IMAGE_MARGIN_END,
        topMargin = CLEAR_IMAGE_MARGIN_TOP,
        gravity = Gravity.END or Gravity.TOP,
      )

      scaleType = ImageView.ScaleType.CENTER
      setImageResource(R.drawable.ic_clear_24)
      setColorFilter(ContextCompat.getColor(context, R.color.appGrey))
      setBackgroundResource(R.drawable.selector_button_dark)
      scaleX = 0.25f
      scaleY = 0.25f
      isVisible = false
      setOnClickListener {
        searchEditText?.setText("")
        clearedListener?.invoke()
      }
    }
    clearImageView?.let { addView(it) }
  }

  private fun setupSuggestionsRecyclerView() {
    suggestionsRecyclerView = RecyclerView(context).apply {
      layoutParams = LayoutHelper.getFrameParams(
        context = context,
        width = LayoutHelper.MATCH_PARENT,
        height = LayoutHelper.WRAP_CONTENT,
        leftMargin = LABEL_MARGIN_START_END,
        rightMargin = LABEL_MARGIN_START_END,
        topMargin = RECYCLER_MARGIN_TOP,
        bottomMargin = RECYCLER_MARGIN_BOTTOM,
        gravity = Gravity.START or Gravity.TOP,
      )
      elevation = dpToPx(4f).toFloat()
      setBackgroundResource(R.drawable.background_search)
      setHasFixedSize(true)
      layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
      adapter = suggestionsAdapter
    }
    suggestionsRecyclerView?.let { addView(it) }
  }

  private fun animateClearShow() {
    animatorSet?.cancel()
    animatorSet = AnimatorSet().apply {
      playTogether(
        ObjectAnimator.ofFloat(clearImageView, View.SCALE_X, 1f),
        ObjectAnimator.ofFloat(clearImageView, View.SCALE_Y, 1f),
      )
      duration = CLEAR_ANIM_DURATION
      interpolator = AccelerateDecelerateInterpolator()
      doOnStart { clearImageView?.isVisible = true }
      doOnEnd { if (it == animatorSet) animatorSet = null }
      start()
    }
  }

  private fun animateClearHide() {
    animatorSet?.cancel()
    animatorSet = AnimatorSet().apply {
      playTogether(
        ObjectAnimator.ofFloat(clearImageView, View.SCALE_X, 0.25f),
        ObjectAnimator.ofFloat(clearImageView, View.SCALE_Y, 0.25f),
      )
      duration = CLEAR_ANIM_DURATION
      interpolator = AccelerateDecelerateInterpolator()
      doOnEnd {
        if (it == animatorSet) animatorSet = null
        clearImageView?.isVisible = false
      }
      start()
    }
  }

  private fun onSuggestionClickedInternal(city: City) {
    suggestionsClickedListener?.invoke(city)
    suggestionsAdapter.setItems(emptyList())
    searchEditText?.let { context.hideKeyboard(it) }
  }

  private class SuggestionsAdapter(
    private val onSuggestionClick: (City) -> Unit
  ) : RecyclerView.Adapter<SuggestionsAdapter.ViewHolder>() {

    private val items = mutableListOf<City>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      return ViewHolder(ItemSuggestionBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun setItems(newItems: List<City>) {
      items.clear()
      items.addAll(newItems)
      notifyDataSetChanged()
    }

    private inner class ViewHolder(private val binding: ItemSuggestionBinding) : RecyclerView.ViewHolder(binding.root) {

      fun bind(item: City) {
        with(binding.suggestion) {
          text = item.fullName
          setOnClickListener { onSuggestionClick.invoke(item) }
        }
      }

    }
  }

  companion object {
    private const val LABEL_HEIGHT = 20
    private const val LABEL_MARGIN_TOP = 24
    private const val LABEL_MARGIN_START_END = 16
    private const val SEARCH_HEIGHT = 48
    private const val SEARCH_MARGIN_TOP = 8
    private const val SEARCH_MARGIN_TOP_SUMMARY = LABEL_MARGIN_TOP + LABEL_HEIGHT + SEARCH_MARGIN_TOP
    private const val SEARCH_PADDING_START = 16F
    private const val SEARCH_PADDING_END = 48F
    private const val CLEAR_IMAGE_DIMEN = 32
    private const val CLEAR_IMAGE_MARGIN_END = 24
    private const val CLEAR_IMAGE_MARGIN_TOP = LABEL_MARGIN_TOP + LABEL_HEIGHT + SEARCH_MARGIN_TOP + (SEARCH_HEIGHT - CLEAR_IMAGE_DIMEN) / 2
    private const val CLEAR_ANIM_DURATION = 300L
    private const val RECYCLER_MARGIN_TOP = SEARCH_MARGIN_TOP_SUMMARY + SEARCH_HEIGHT
    private const val RECYCLER_MARGIN_BOTTOM = 24
  }
}