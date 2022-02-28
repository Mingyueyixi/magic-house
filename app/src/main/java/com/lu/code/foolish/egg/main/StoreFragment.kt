package com.lu.code.foolish.egg.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.lu.code.foolish.egg.R
import com.lu.code.foolish.egg.databinding.FragmentStoreBinding
import com.lu.code.foolish.egg.main.plugin.FuckDialogFragment
import com.lu.code.foolish.egg.main.store.ItemModel
import com.lu.code.foolish.egg.main.store.TitleModel
import com.lu.code.foolish.egg.ui.BaseFragment
import com.lu.code.foolish.egg.ui.LifecycleAutoViewBinding
import com.lu.code.foolish.egg.ui.recycler.MultiAdapter
import com.lu.code.foolish.egg.ui.recycler.MultiItemType
import com.lu.code.foolish.egg.ui.recycler.MultiViewHolder
import com.lu.code.foolish.egg.util.PackageUtil
import com.lu.code.foolish.egg.util.log.LogUtil

class StoreFragment : BaseFragment() {
    private var binding: FragmentStoreBinding by LifecycleAutoViewBinding()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val flexboxLayoutManager: FlexboxLayoutManager = object : FlexboxLayoutManager(
            context, FlexDirection.ROW, FlexWrap.WRAP
        ) {
            override fun canScrollVertically(): Boolean {
                //不许垂直滚动
                return false
            }
        }

        binding = FragmentStoreBinding.inflate(inflater, container, false)

        binding.mStoreRecyclerView.layoutManager = flexboxLayoutManager
        binding.mStoreRecyclerView.adapter = MultiAdapter<ItemModel>()
            .addData(
                TitleModel("测试"),
                ItemModel("测试", null),
                ItemModel("安全截图", null),
                TitleModel("禁止"),
                ItemModel(
                    "对话框",
                    FuckDialogFragment::class.java
                )

            )
            .addItemType(object : MultiItemType<ItemModel> {
                override fun getItemViewType(
                    adapter: MultiAdapter<ItemModel>,
                    itemModel: ItemModel,
                    position: Int
                ): Boolean {
                    return itemModel.javaClass == TitleModel::class.java
                }

                override fun createViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): MultiViewHolder<ItemModel> {

                    return object : MultiViewHolder<ItemModel>(
                        inflater.inflate(
                            R.layout.item_store_plugin_sub,
                            parent,
                            false
                        )
                    ) {
                        var tvItemPluginSub: TextView =
                            itemView.findViewById<TextView>(R.id.tvItemPluginSub)
//
//                            init {
//                                itemView.setOnClickListener {
//
//                                }
//                            }

                        override fun onBindView(
                            adapter: MultiAdapter<ItemModel>,
                            itemModel: ItemModel,
                            position: Int,
                        ) {
                            tvItemPluginSub.text = itemModel.name
                            itemView.setOnClickListener {
                                if (itemModel.pageCls != null)
                                    PluginConfigActivity.start(
                                        itemView.context,
                                        itemModel.pageCls!!
                                    )
                            }
                        }

                    }
                }
            })
            .addItemType(object : MultiItemType<ItemModel> {
                override fun getItemViewType(
                    adapter: MultiAdapter<ItemModel>,
                    itemModel: ItemModel,
                    position: Int
                ): Boolean {
                    return itemModel.javaClass == ItemModel::class.java
                }

                override fun createViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): MultiViewHolder<ItemModel> {
                    var itemView = LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_store_plugin_cake, parent, false)

                    return object : MultiViewHolder<ItemModel>(itemView) {
                        var tvName = itemView.findViewById<TextView>(R.id.tvItemPluginName)
                        override fun onBindView(
                            adapter: MultiAdapter<ItemModel>,
                            itemModel: ItemModel,
                            position: Int
                        ) {
                            tvName.text = itemModel.name
                        }
                    }
                }
            })
        return binding.root
    }

    override fun onResume() {
        super.onResume()
//        Log.d(">>>", ClassUtil.getClassList(context, "com.lu.code.foolish.egg.hook").toString())
        PackageUtil.getInstallPackageNameListByShell().onEach {
            LogUtil.d(it)
        }

    }
}