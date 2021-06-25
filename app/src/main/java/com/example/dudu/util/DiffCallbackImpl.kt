package com.example.dudu.util

import androidx.recyclerview.widget.DiffUtil

class DiffCallbackImpl<T>(
    private val oldItems: List<T>,
    private val newItems: List<T>,
    private val areTheSame: (oldItem: T, newItem: T) -> Boolean =
        { oldItem , newItem -> oldItem == newItem },
    private val areContentsTheSame: (oldItem: T, newItem: T) -> Boolean =
        { oldItem , newItem -> oldItem == newItem },
    private val getChangePayload: (oldItem: T, newItem: T) -> Any =
        { _, _ -> Any() }
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldItems.size
    }

    override fun getNewListSize(): Int {
        return newItems.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldItems[oldItemPosition]
        val newItem = newItems[newItemPosition]
        return areTheSame(oldItem, newItem)
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldItems[oldItemPosition]
        val newItem = newItems[newItemPosition]
        return areContentsTheSame(oldItem, newItem)
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any {
        val oldItem = oldItems[oldItemPosition]
        val newItem = newItems[newItemPosition]
        return getChangePayload(oldItem, newItem)
    }
}