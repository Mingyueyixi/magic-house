package com.lu.code.foolish.egg.ui.recycler

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

open class MultiAdapter<E>() : RecyclerView.Adapter<MultiViewHolder>() {
    private val data: MutableList<E>
    private val itemTypeList: MutableList<MultiItemType<E>>

    init {
        data = ArrayList()
        this.itemTypeList = ArrayList()
    }

    fun addItemTypes(vararg itemTypeArrays: MultiItemType<E>): MultiAdapter<E> {
        this.itemTypeList.addAll(itemTypeArrays)
        return this
    }

    fun addItemType(itemType: MultiItemType<E>): MultiAdapter<E> {
        this.itemTypeList.add(itemType)
        return this
    }

    fun setData(data: List<E>) {
        this.data.clear()
        this.data.addAll(data)
    }

    fun addData(data: List<E>) {
        this.data.addAll(data)
    }

    fun getData(): List<E> {
        return data
    }

    fun getListeners(): List<MultiItemType<E>> {
        return itemTypeList
    }

    override fun getItemViewType(position: Int): Int {
        for (i in itemTypeList.indices) {
            val itemType = itemTypeList[i]
            if (itemType.getItemViewType(this, position)) {
                return i
            }
        }
        return 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultiViewHolder {
        return itemTypeList[viewType].createViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: MultiViewHolder, position: Int) {
        holder.onBindView(holder, position)
    }

    override fun getItemCount(): Int {
        return data.size
    }

}