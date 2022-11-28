package com.lu.magic.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.lu.magic.*
import com.lu.magic.databinding.LayoutListBinding
import com.lu.magic.store.ItemModel
import com.lu.magic.store.ModuleModel
import com.lu.magic.store.TitleModel
import com.lu.magic.ui.BaseFragment
import com.lu.magic.ui.LifecycleAutoViewBinding
import com.lu.magic.ui.recycler.MultiAdapter
import com.lu.magic.ui.recycler.MultiItemType
import com.lu.magic.ui.recycler.MultiViewHolder
import com.lu.magic.util.PackageUtil
import com.lu.magic.util.config.SheetName

class StoreFragment : BaseFragment() {
    private var binding: LayoutListBinding by LifecycleAutoViewBinding()

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

        binding = LayoutListBinding.inflate(inflater, container, false)
        binding.mRecyclerView.layoutManager = flexboxLayoutManager
        binding.mRecyclerView.adapter = MultiAdapter<ItemModel>()
            .addData(
                TitleModel("测试"),
                ItemModel("测试", ModuleModel()),
                ItemModel("安全截图", ModuleModel()),
                TitleModel("禁止"),
                ItemModel("对话框", ModuleModel("对话框-禁止显示", SheetName.FUCK_DIALOG)),
                ItemModel(
                    "震动器",
                    ModuleModel("禁止震动", SheetName.FUCK_VIBRATOR)
                ),
                TitleModel("位置"),
                ItemModel("AMap", ModuleModel("高德地图", SheetName.AMAP_LOCATION)),
                TitleModel("开发工具"),
                ItemModel("视图捕获", ModuleModel("视图捕获", SheetName.VIEW_CATCH)),
                TitleModel("其他"),
                ItemModel("屏幕旋转", ModuleModel("屏幕旋转模式", SheetName.FUCK_SCREEN_ORIENTATION)),
                ItemModel("锁定视图", ModuleModel("锁定视图", SheetName.VIEW_LOCK)),

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
                    adapter: MultiAdapter<ItemModel>,
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

                        override fun onBindView(
                            adapter: MultiAdapter<ItemModel>,
                            itemModel: ItemModel,
                            position: Int,
                        ) {
                            tvItemPluginSub.text = itemModel.name
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
                    adapter: MultiAdapter<ItemModel>,
                    parent: ViewGroup,
                    viewType: Int
                ): MultiViewHolder<ItemModel> {

                    var itemView = LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_store_plugin_cake, parent, false)

                    return object : MultiViewHolder<ItemModel>(itemView) {
                        var tvName = itemView.findViewById<TextView>(R.id.tvItemPluginName)

                        init {
                            itemView.setOnClickListener {
                                var item = adapter.getItem(layoutPosition)
                                item?.let {
                                    ModuleProviders.get(item.module.sheet)
                                        ?.onEntry(itemView.context, item)
                                }

                            }
                        }

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
//            LogUtil.d(it)
        }

    }
}