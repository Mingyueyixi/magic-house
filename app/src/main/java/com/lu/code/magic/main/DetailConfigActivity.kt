package com.lu.code.magic.main

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.lu.code.magic.magic.databinding.LayoutContainerBinding
import com.lu.code.magic.main.amap.FuckAMapFragment
import com.lu.code.magic.main.fuckdialog.FuckDialogFragment
import com.lu.code.magic.main.store.ItemModel
import com.lu.code.magic.ui.BaseActivity
import com.lu.code.magic.ui.FragmentNavigation
import com.lu.code.magic.util.SingleStoreUtil
import com.lu.code.magic.util.config.SheetName
import com.lu.code.magic.util.log.LogUtil

class DetailConfigActivity : BaseActivity() {
    private lateinit var binding: LayoutContainerBinding
    private lateinit var fragmentNavigation: FragmentNavigation
    private lateinit var routeModel: ItemModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutContainerBinding.inflate(layoutInflater)
        initData()
        initView()
    }

    private fun initData() {
        var preItemModel = SingleStoreUtil.get(ItemModel::class.java)
        if (preItemModel == null) {
            finish()
            return
        }
        routeModel = preItemModel
    }

    private fun initView() {
        if (routeModel.page.land) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
        initToolBar()
        setContentView(binding.root)
        attachPage()
    }

    private fun attachPage() {
        fragmentNavigation = FragmentNavigation(this, binding.fragmentContainer)
        when (routeModel.page.sheet) {
            SheetName.AMAP_LOCATION -> {
                binding.appBarLayout.toolbar.title = "选择位置"
                fragmentNavigation.navigate(FuckAMapFragment())
            }
            SheetName.FUCK_DIALOG -> {
                fragmentNavigation.navigate(FuckDialogFragment())
            }
            else -> {
                fragmentNavigation.navigate(Fragment())
            }
        }
    }

    private fun initToolBar() {
        var toolbar = binding.appBarLayout.toolbar;
        toolbar.title = routeModel.page.title
        //在设置setSupportActionBar之前设置toolbar标题，否则无效
        setSupportActionBar(toolbar)
        //设置导航点击监听，在setSupportActionBar之后，否则无效
        toolbar.setNavigationOnClickListener {
            if (!fragmentNavigation.navigateBack()) {
                finish()
            }
        }
    }

    fun getToolBar(): Toolbar {
        return binding.appBarLayout.toolbar
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtil.d(">>>", "Detsory")
    }
}