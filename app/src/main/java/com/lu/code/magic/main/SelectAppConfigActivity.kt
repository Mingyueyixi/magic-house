package com.lu.code.magic.main;

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.lu.code.magic.magic.databinding.LayoutContainerBinding
import com.lu.code.magic.main.fuckdialog.FuckDialogFragment
import com.lu.code.magic.main.store.ItemModel
import com.lu.code.magic.ui.BaseActivity
import com.lu.code.magic.ui.FragmentNavigation
import com.lu.code.magic.util.SingleStoreUtil
import com.lu.code.magic.util.config.SheetName

class SelectAppConfigActivity : BaseActivity() {
    private lateinit var fragmentNavigation: FragmentNavigation
    private lateinit var viewModel: MagicConfigViewModel
    private lateinit var binding: LayoutContainerBinding
    lateinit var routeItem: ItemModel


    companion object {
        fun start(context: Context, item: ItemModel) {
            if (item.page.sheet.isEmpty()) {
                return
            }
            val intent = Intent(context, SelectAppConfigActivity::class.java)
            intent.putExtra(Constant.KEY_ROUTE_ITEM, item)
            context.startActivity(intent)
        }
    }


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutContainerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var route = intent.getSerializableExtra(Constant.KEY_ROUTE_ITEM) as ItemModel?
        if (route == null) {
            finish()
            return
        }
        routeItem = route
        if (routeItem.page.sheet.isEmpty()) {
            return
        }
        fragmentNavigation = FragmentNavigation(this, binding.fragmentContainer)
        viewModel = ViewModelProvider(this).get(MagicConfigViewModel::class.java)

        initToolBar()
        initAppListPage()
    }

    override fun onBackPressed() {
        var backResult = fragmentNavigation.navigateBack()
        if (!backResult) {
            super.onBackPressed()
        }
    }


    private fun initToolBar() {
        binding.toolbar.title = routeItem.page.title
        //在设置setSupportActionBar之前设置toolbar标题，否则无效
        setSupportActionBar(binding.toolbar)
        //设置导航点击监听，在setSupportActionBar之后，否则无效
        binding.toolbar.setNavigationOnClickListener {
            if (!fragmentNavigation.navigateBack()) {
                finish()
            }
        }

    }

    private fun initAppListPage() {
        var fragment: Fragment = SelectAppFragment()
        fragmentNavigation.navigate(fragment)
    }

    fun showConfigPage(itemData: AppListModel) {
        viewModel.appListModel = itemData
        SingleStoreUtil.put(routeItem)
        SingleStoreUtil.put(itemData)

        when (routeItem.page.sheet) {
            SheetName.FUCK_DIALOG -> {
                fragmentNavigation.navigate(FuckDialogFragment::class.java.newInstance())
            }
            SheetName.AMAP_LOCATION -> {
                var intent = Intent(this, DetailConfigActivity::class.java)
                startActivity(intent)
            }
            else -> {
                fragmentNavigation.navigate(Fragment::class.java.newInstance())
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        SingleStoreUtil.remove(AppListModel::class.java)
        SingleStoreUtil.remove(routeItem.javaClass)
    }
}


