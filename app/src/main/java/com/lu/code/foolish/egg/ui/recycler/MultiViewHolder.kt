package com.lu.code.foolish.egg.ui.recycler

import android.view.View
import androidx.recyclerview.widget.RecyclerView

open abstract class MultiViewHolder<E> (itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun onBindView(
        adapter: MultiAdapter<E>,
        itemModel: E,
        position: Int
    )

}