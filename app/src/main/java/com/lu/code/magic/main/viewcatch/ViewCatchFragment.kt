package com.lu.code.magic.main.viewcatch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.kyleduo.switchbutton.SwitchButton
import com.lu.code.magic.bean.ViewCatchConfig
import com.lu.code.magic.magic.R
import com.lu.code.magic.magic.databinding.LayoutListBinding
import com.lu.code.magic.main.AppListModel
import com.lu.code.magic.main.store.TitleModel
import com.lu.code.magic.ui.BindingFragment
import com.lu.code.magic.ui.recycler.MultiAdapter
import com.lu.code.magic.ui.recycler.MultiViewHolder
import com.lu.code.magic.ui.recycler.SimpleItemType
import com.lu.code.magic.util.GsonUtil
import com.lu.code.magic.util.SingleStoreUtil
import com.lu.code.magic.util.config.ConfigUtil
import com.lu.code.magic.util.config.SheetName
import java.util.ArrayList

class ViewCatchFragment : BindingFragment<LayoutListBinding>() {
    private lateinit var appListModel: AppListModel
    private lateinit var mViewDataList: ArrayList<Model>
    lateinit var config: ViewCatchConfig

    override fun onViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): LayoutListBinding {
        appListModel = SingleStoreUtil.get(AppListModel::class.java)
        val routeItem = SingleStoreUtil.get(TitleModel::class.java)

        var c: ViewCatchConfig? = ConfigUtil.getCell(
            SheetName.VIEW_CATCH,
            appListModel.packageName,
            ViewCatchConfig::class.java
        )
        if (c == null) {
            c = ViewCatchConfig()
        }
        if (c.data == null) {
            c.data = ViewCatchConfig.Feature()
        }
        this.config = c

        return LayoutListBinding.inflate(inflater, container, false)
    }

    override fun onBackPressed(): Boolean {
        ConfigUtil.setCell(SheetName.VIEW_CATCH, appListModel.packageName, config)
        return super.onBackPressed()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mViewDataList = arrayListOf(
            Model("ActivityResume", config.data.activityResume),
            Model("FragmentResume", config.data.fragmentResume),
            Model("ViewClick", config.data.viewClick)
        )

        binding.mRecyclerView.adapter = object : MultiAdapter<Model>() {

        }.setData(mViewDataList)
            .addItemType(object : SimpleItemType<Model>() {
                override fun createViewHolder(
                    adapter: MultiAdapter<Model>,
                    parent: ViewGroup,
                    viewType: Int
                ): MultiViewHolder<Model> {
                    var view = layoutInflater.inflate(R.layout.item_enable_list, parent, false)
                    return object : MultiViewHolder<Model>(view) {
                        private var sbSwitch: SwitchButton
                        private var tvBodyTitle: TextView

                        init {
                            tvBodyTitle = itemView.findViewById<TextView>(R.id.tvBodyTitle)
                            sbSwitch = itemView.findViewById<SwitchButton>(R.id.sbEnableItem)
                            itemView.findViewById<TextView>(R.id.tvBodySubTitle)
                                .visibility = View.GONE

                            sbSwitch.setOnClickListener {
                                var model = adapter.getData()[layoutPosition]
                                model.enable = sbSwitch.isChecked
                                when (model.name) {
                                    "ActivityResume" -> {
                                        config.data.activityResume = model.enable
                                    }
                                    "FragmentResume" -> {
                                        config.data.fragmentResume = model.enable
                                    }
                                    "ViewClick" -> {
                                        config.data.viewClick = model.enable
                                    }
                                }

                            }
                        }

                        override fun onBindView(
                            adapter: MultiAdapter<Model>,
                            itemModel: Model,
                            position: Int
                        ) {
                            tvBodyTitle.text = itemModel.name
                            sbSwitch.isChecked = itemModel.enable
                        }

                    }
                }

            })

        binding.mRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
    }

    class Model(var name: String, var enable: Boolean) {

    }
}
