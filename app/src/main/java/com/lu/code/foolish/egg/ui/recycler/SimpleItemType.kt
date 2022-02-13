package com.lu.code.foolish.egg.ui.recycler

open abstract class SimpleItemType<E> : MultiItemType<E> {
    override fun getItemViewType(adapter: MultiAdapter<E>, position: Int): Boolean {
        return true
    }

}