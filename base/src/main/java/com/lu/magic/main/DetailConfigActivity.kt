package com.lu.magic.main

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.lu.magic.ModuleProviders
import com.lu.magic.base.databinding.LayoutContainerBinding
import com.lu.magic.store.ItemModel
import com.lu.magic.ui.BaseToolBarActivity
import com.lu.magic.ui.FragmentNavigation
import com.lu.magic.util.SingleClassStoreUtil
import com.lu.magic.config.ModuleId

class DetailConfigActivity : BaseToolBarActivity() {
    private lateinit var pageFragment: Fragment
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
        var preItemModel = SingleClassStoreUtil.get(ItemModel::class.java)
        if (preItemModel == null) {
            finish()
            return
        }
        routeModel = preItemModel
    }

    private fun initView() {
        ModuleProviders.get(routeModel.module.moduleId).let {
            if (it != null) {
                var fragmentFactory = it.detailFragmentFactory
                if (fragmentFactory == null) {
                    finish()
                    return
                }
                fragmentFactory.create().let { frag ->
                    if (frag == null) {
                        finish()
                        return
                    }
                    pageFragment = frag
                }
            } else {
                finish()
                return
            }
        }

        if (routeModel.module.land) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
        initToolBar()
        setContentView(binding.root)
        attachPage()
    }

    private fun attachPage() {
        fragmentNavigation = FragmentNavigation(this, binding.fragmentContainer)
        when (routeModel.module.moduleId) {
            ModuleId.AMAP_LOCATION -> {
                binding.appBarLayout.toolbar.title = "选择位置"
            }
//            else -> {
//                fragmentNavigation.navigate(Fragment())
//            }
        }
        pageFragment?.let {
            fragmentNavigation.navigate(it)
        }
    }

    private fun initToolBar() {
        var toolbar = binding.appBarLayout.toolbar;
        toolbar.title = routeModel.module.title
        //在设置setSupportActionBar之前设置toolbar标题，否则无效
        setSupportActionBar(toolbar)
        //设置导航点击监听，在setSupportActionBar之后，否则无效
        toolbar.setNavigationOnClickListener {
            onBackPressed()
//            if (!fragmentNavigation.navigateBack()) {
//            }
        }
    }

    override fun getToolBar(): Toolbar {
        return binding.appBarLayout.toolbar
    }

    override fun onDestroy() {
        super.onDestroy()
        com.lu.magic.util.log.LogUtil.d(">>>", "Detsory")
    }
}