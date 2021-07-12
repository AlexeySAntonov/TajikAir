package com.aleksejantonov.tajikair.ui.main.search

import androidx.recyclerview.widget.DiffUtil

class CitiesDiffCallback(
  private val oldItems: List<CityItem>,
  private val newItems: List<CityItem>
) : DiffUtil.Callback() {

  override fun getOldListSize(): Int = oldItems.size

  override fun getNewListSize(): Int = newItems.size

  override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
    return oldItems[oldItemPosition].iata == newItems[newItemPosition].iata
  }

  override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
    return oldItems[oldItemPosition] == newItems[newItemPosition]
  }
}