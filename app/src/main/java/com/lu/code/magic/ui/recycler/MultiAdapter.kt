package com.lu.code.magic.ui.recycler

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

open class MultiAdapter<E>() : RecyclerView.Adapter<MultiViewHolder<E>>() {
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

    fun addData(data: List<E>): MultiAdapter<E> {
        this.data.addAll(data)
        return this
    }

    fun addData(vararg ele: E): MultiAdapter<E> {
        this.data.addAll(ele)
        return this
    }

    fun getData(): List<E> {
        return data
    }

    fun getListeners(): List<MultiItemType<E>> {
        return itemTypeList
    }

    fun getItem(position: Int): E? {
        return data[position];
    }

    override fun getItemViewType(position: Int): Int {
        val itemModel = data[position]
        for (i in itemTypeList.indices) {
            val itemType = itemTypeList[i]
            if (itemType.getItemViewType(this, itemModel, position)) {
                return i
            }
        }
        return 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultiViewHolder<E> {
        return itemTypeList[viewType].createViewHolder(this, parent, viewType)
    }

    override fun onBindViewHolder(holder: MultiViewHolder<E>, position: Int) {
        holder.onBindView(this, data[position], position)
    }

    override fun getItemCount(): Int {
        return data.size
    }

}