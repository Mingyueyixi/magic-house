package com.lu.code.foolish.egg.ui.recycler

import android.view.View
import androidx.recyclerview.widget.RecyclerView

open abstract class MultiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun onBindView(holder: MultiViewHolder, position: Int)

}