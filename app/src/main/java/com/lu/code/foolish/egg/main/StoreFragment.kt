package com.lu.code.foolish.egg.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.lu.code.foolish.egg.R
import com.lu.code.foolish.egg.databinding.FragmentStoreBinding
import com.lu.code.foolish.egg.hook.DisableFlagSecurePlugin
import com.lu.code.foolish.egg.hook.TestHookPlugin
import com.lu.code.foolish.egg.ui.BaseFragment
import com.lu.code.foolish.egg.ui.recycler.MultiAdapter
import com.lu.code.foolish.egg.ui.recycler.MultiItemType
import com.lu.code.foolish.egg.ui.recycler.MultiViewHolder
import com.lu.code.foolish.egg.util.ClassUtil
import com.lu.code.foolish.egg.util.log.LogUtil

class StoreFragment : BaseFragment() {
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
        withBindingWrap {
            FragmentStoreBinding.inflate(inflater, container, false)
        }.apply {
            binding.mStoreRecyclerView.layoutManager = flexboxLayoutManager
            binding.mStoreRecyclerView.adapter = MultiAdapter<PluginItemModel>()
                .addData(
                    //由于gradle配置xpose库只参与编译，没实际打包，TestHookPlugin等实际找不到class，compileOnly
                    //参数不传类型？而传string？
                    //todo 待定
                    PluginItemModel("测试", "com.lu.code.foolish.egg.hook.TestHookPlugin"),
                    PluginItemModel(
                        "安全截图",
                        "com.lu.code.foolish.egg.hook.TestHookPlugin.DisableFlagSecurePlugin"
                    )
                )
                .addItemType(object : MultiItemType<PluginItemModel> {
                    override fun getItemViewType(
                        adapter: MultiAdapter<PluginItemModel>,
                        itemModel: PluginItemModel,
                        position: Int
                    ): Boolean {
                        return true
                    }

                    override fun createViewHolder(
                        parent: ViewGroup,
                        viewType: Int
                    ): MultiViewHolder<PluginItemModel> {
                        return object : MultiViewHolder<PluginItemModel>(
                            inflater.inflate(
                                R.layout.item_store_plugin_sub,
                                parent,
                                false
                            )
                        ) {
                            var tvItemPluginSub: TextView =
                                itemView.findViewById<TextView>(R.id.tvItemPluginSub)

                            override fun onBindView(
                                adapter: MultiAdapter<PluginItemModel>,
                                itemModel: PluginItemModel,
                                position: Int,
                            ) {
                                tvItemPluginSub.text = itemModel.name
                            }

                        }
                    }
                })
                .addItemType(object : MultiItemType<PluginItemModel> {
                    override fun getItemViewType(
                        adapter: MultiAdapter<PluginItemModel>,
                        itemModel: PluginItemModel,
                        position: Int
                    ): Boolean {
//                        return itemModel.clazz != null
                        return true
                    }

                    override fun createViewHolder(
                        parent: ViewGroup,
                        viewType: Int
                    ): MultiViewHolder<PluginItemModel> {
                        var itemView = LayoutInflater.from(parent.context)
                            .inflate(R.layout.item_store_plugin_cake, parent, false)
                        return object : MultiViewHolder<PluginItemModel>(itemView) {
                            var tvName = itemView.findViewById<TextView>(R.id.tvItemPluginName)
                            override fun onBindView(
                                adapter: MultiAdapter<PluginItemModel>,
                                itemModel: PluginItemModel,
                                position: Int
                            ) {
                                tvName.text = itemModel.name
                            }

                        }
                    }

                })
        }
        return bindingWrap.binding.root
    }

    override fun onResume() {
        super.onResume()
        Log.d(">>>", ClassUtil.getClassList(context,"com.lu.code.foolish.egg.hook").toString())

    }
}