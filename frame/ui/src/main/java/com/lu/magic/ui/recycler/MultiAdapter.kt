package com.lu.magic.ui.recycler

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import java.util.concurrent.CopyOnWriteArrayList

open class MultiAdapter<E> : RecyclerView.Adapter<MultiViewHolder<E>>() {
    private val data: MutableList<E>
    private val itemTypeList: MutableList<MultiItemType<E>>
    private var customDataObserver: AdapterDataObserver

    init {
        data = CopyOnWriteArrayList()
        this.itemTypeList = CopyOnWriteArrayList()
        customDataObserver = object : AdapterDataObserver() {

        }
    }

    open fun setDataObserver(dataObserver: AdapterDataObserver): MultiAdapter<E> {
        if (hasObservers()) {
            try {
                unregisterAdapterDataObserver(customDataObserver)
            } catch (e: Exception) {
                e.toString()
            }
        }
        customDataObserver = dataObserver
        registerAdapterDataObserver(dataObserver)
        return this
    }

    open fun removeDataObserver() {
        unregisterAdapterDataObserver(customDataObserver)
    }

    open fun addItemTypes(vararg itemTypeArrays: MultiItemType<E>): MultiAdapter<E> {
        this.itemTypeList.addAll(itemTypeArrays)
        return this
    }

    open fun addItemType(itemType: MultiItemType<E>): MultiAdapter<E> {
        this.itemTypeList.add(itemType)
        return this
    }

    open fun setData(data: List<E>): MultiAdapter<E> {
        this.data.clear()
        this.data.addAll(data)
        return this
    }

    open fun updateData(data: List<E>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    open fun updateData(ele: E) {
        var index = data.indexOf(ele)
        updateData(index)
    }

    open fun updateData(position: Int) {
        if (position < 0 || position >= data.size) {
            return
        }
        notifyItemChanged(position)
    }


    open fun addData(data: List<E>): MultiAdapter<E> {
        this.data.addAll(data)
        return this
    }

    open fun addData(vararg ele: E): MultiAdapter<E> {
        this.data.addAll(ele)
        return this
    }

    open fun getData(): MutableList<E> {
        return data
    }

    open fun getItem(position: Int): E? {
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

    interface DataObserver<E> {
        fun onDataChanged(atapter: MultiAdapter<E>)
    }

}