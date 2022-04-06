package com.lu.code.magic.main;

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Window
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.lu.code.magic.magic.databinding.ActivityPluginConfigBinding
import com.lu.code.magic.main.fuckdialog.FuckDialogFragment
import com.lu.code.magic.main.store.ItemModel
import com.lu.code.magic.main.store.PageModel
import com.lu.code.magic.ui.BaseActivity
import com.lu.code.magic.ui.FragmentNavigation
import com.lu.code.magic.util.config.SheetName
import com.lu.code.magic.util.log.LogUtil

class MagicConfigActivity : BaseActivity() {
    private lateinit var fragmentNavigation: FragmentNavigation
    private lateinit var viewModel: MagicConfigViewModel
    private lateinit var binding: ActivityPluginConfigBinding
    lateinit var routeItem: ItemModel


    companion object {
        private const val KEY_ROUTE_ITEM = "KEY_ROUTE_ITEM"

        fun start(context: Context, item: ItemModel) {
            if (item.page.sheet.isEmpty()) {
                return
            }
            val intent = Intent(context, MagicConfigActivity::class.java)
            intent.putExtra(KEY_ROUTE_ITEM, item)
            context.startActivity(intent)
        }
    }


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = ActivityPluginConfigBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var route = intent.getSerializableExtra(KEY_ROUTE_ITEM) as ItemModel?
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
        var fragment: Fragment = when (routeItem.page.sheet) {
            SheetName.FUCK_DIALOG -> FuckDialogFragment::class.java.newInstance()
            else -> {
                Fragment::class.java.newInstance()
            }
        }

        LogUtil.d(">>>show $fragment")
        fragmentNavigation.navigate(fragment)
//        fragmentNavigation.navigate(routeItem.page.pageCls!!)
    }

}


