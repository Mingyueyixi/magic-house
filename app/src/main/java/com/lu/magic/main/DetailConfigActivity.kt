package com.lu.magic.main

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.lu.magic.amap.FuckAMapFragment
import com.lu.magic.base.databinding.LayoutContainerBinding
import com.lu.magic.main.fuckdialog.FuckDialogFragment
import com.lu.magic.screen.ScreenOrientationFragment
import com.lu.magic.main.viewcatch.ViewCatchFragment
import com.lu.magic.main.viewlock.ViewLockFragment
import com.lu.magic.store.ItemModel
import com.lu.magic.ui.BaseToolBarActivity
import com.lu.magic.ui.FragmentNavigation
import com.lu.magic.util.SingleStoreUtil
import com.lu.magic.util.config.SheetName
import com.lu.magic.util.log.LogUtil

class DetailConfigActivity : BaseToolBarActivity() {
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
            SheetName.FUCK_VIBRATOR -> {
                //暂时没有其他配置。例如禁止长时间震动，修改震动时长，意义不大。
            }
            SheetName.FUCK_SCREEN_ORIENTATION -> {
                //根据重力、固定等，待添加
                fragmentNavigation.navigate(ScreenOrientationFragment())
            }
            SheetName.VIEW_CATCH -> {
                fragmentNavigation.navigate(ViewCatchFragment())
            }
            SheetName.VIEW_LOCK -> {
                fragmentNavigation.navigate(ViewLockFragment())
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