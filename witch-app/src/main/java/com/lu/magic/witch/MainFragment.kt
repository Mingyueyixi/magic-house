package com.lu.magic.witch

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.lu.magic.R
import com.lu.magic.databinding.FragMainBinding
import com.lu.magic.ui.BindingFragment
import com.lu.magic.ui.recycler.MultiAdapter
import com.lu.magic.ui.recycler.MultiViewHolder
import com.lu.magic.ui.recycler.SimpleItemType


class MainFragment : BindingFragment<FragMainBinding>() {
    private lateinit var mActivityLauncher: ActivityResultLauncher<String>
    override fun onViewBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): FragMainBinding {
        return FragMainBinding.inflate(inflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvTagShow.layoutManager = object : FlexboxLayoutManager(
            context, FlexDirection.ROW, FlexWrap.WRAP
        ) {
            override fun canScrollVertically(): Boolean {
                //许垂直滚动
                return true
            }
        }

        binding.rvTagShow.adapter = MultiAdapter<ItemData>()
            .addData(ItemData("高德地图") {
                routeAMapPage()
            })
            .addItemType(object : SimpleItemType<ItemData>() {
                override fun createViewHolder(adapter: MultiAdapter<ItemData>, parent: ViewGroup, viewType: Int): MultiViewHolder<ItemData> {
                    return object : MultiViewHolder<ItemData>(LayoutInflater.from(parent.context).inflate(R.layout.item_text, parent, false)) {
                        init {
                            itemView.setOnClickListener {
                                adapter.getItem(layoutPosition)!!.action.run()
                            }
                        }

                        override fun onBindView(adapter: MultiAdapter<ItemData>, itemModel: ItemData, position: Int) {
                            itemView.findViewById<TextView>(R.id.tvTag).text = itemModel.text

                        }
                    }
                }
            })


    }

    private fun routeAMapPage() {
        val intent = Intent(context, MultipleActivity::class.java)
        intent.setAction(com.lu.magic.bridge.BridgeConstant.CODE_MULTI_ACTIVITY_ACTION)
        intent.putExtra(com.lu.magic.bridge.BridgeConstant.CODE_INTENT_MULTIPLE_PAGE, "amap")
        intent.putExtra(com.lu.magic.bridge.BridgeConstant.CODE_INTENT_REQUEST_DATA, "{}")
        startActivityForResult(intent, com.lu.magic.bridge.BridgeConstant.CODE_ACTIVITY_REQUEST_CODE)

    }

    class ItemData(var text: String, var action: Runnable)
}