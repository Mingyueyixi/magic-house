package com.lu.magic.ui.recycler

import android.view.View
import androidx.recyclerview.widget.RecyclerView

open abstract class MultiViewHolder<E> : RecyclerView.ViewHolder {

    constructor(itemView: View) : super(itemView) {
        onInit()
    }


    open fun onInit() {

    }

    abstract fun onBindView(
        adapter: MultiAdapter<E>,
        itemModel: E,
        position: Int
    )

}