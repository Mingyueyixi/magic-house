package com.lu.magic.ui.recycler

import android.view.ViewGroup

interface MultiItemType<E> {
    fun getItemViewType(adapter: MultiAdapter<E>, itemModel: E, position: Int): Boolean
    fun createViewHolder(adapter: MultiAdapter<E>, parent: ViewGroup, viewType: Int): MultiViewHolder<E>
}