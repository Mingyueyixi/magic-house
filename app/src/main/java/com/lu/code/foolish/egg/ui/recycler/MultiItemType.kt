package com.lu.code.foolish.egg.ui.recycler

import android.view.ViewGroup

interface MultiItemType<E> {
    fun getItemViewType(adapter: MultiAdapter<E>, itemModel: E, position: Int): Boolean
    fun createViewHolder(parent: ViewGroup, viewType: Int): MultiViewHolder<E>
}