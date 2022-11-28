package com.lu.magic.ui.recycler

open abstract class SimpleItemType<E> : MultiItemType<E> {
    override fun getItemViewType(adapter: MultiAdapter<E>, itemModel: E, position: Int): Boolean {
        return true
    }

}