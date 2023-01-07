package com.lu.magic.catchlog

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration


class LastItemMarginBottomDecoration(var bottomMargin: Int) : ItemDecoration() {


    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        parent.adapter?.let {
            if (parent.getChildLayoutPosition(view) == it.itemCount - 1) {
                outRect.bottom = bottomMargin
            }
        }
    }

}
