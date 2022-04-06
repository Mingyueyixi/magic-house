package com.lu.code.magic.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.lu.code.magic.bean.FuckDialogConfig
import com.lu.code.magic.magic.R
import com.lu.code.magic.magic.databinding.ActivityMainBinding
import com.lu.code.magic.provider.XPreference
import com.lu.code.magic.ui.BaseActivity
import com.lu.code.magic.util.FragmentUtil
import com.lu.code.magic.util.config.ConfigUtil
import com.lu.code.magic.util.log.LogUtil


class MainActivity : BaseActivity() {
    private lateinit var pageModelList: ArrayList<PageModel>
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        pageModelList = arrayListOf(
            PageModel(R.drawable.ic_icon_store, R.string.store) {
                FragmentUtil.newInstance<StoreFragment>()
            },
            PageModel(R.drawable.ic_icon_log, R.string.log) {
                FragmentUtil.newInstance<LogFragment>()
            },
            PageModel(R.drawable.ic_icon_about, R.string.about) {
                FragmentUtil.newInstance<AboutFragment>()
            }
        )

        pageModelList.forEachIndexed { index, it ->
            var tab = binding.mainBottomTabLayout.newTab()
            tab.setIcon(it.iconId)
            tab.setText(it.textId)
            if (index == 0) {
                binding.mainBottomTabLayout.addTab(tab, index, true)
            } else {
                binding.mainBottomTabLayout.addTab(tab)
            }
        }
        //水平滚动
        binding.mainContentViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.mainContentViewPager.adapter = PageAdapter(this, pageModelList)
        binding.mainContentViewPager.offscreenPageLimit = pageModelList.size
        TabLayoutMediator(binding.mainBottomTabLayout,
            binding.mainContentViewPager,
            true,
            object : TabLayoutMediator.TabConfigurationStrategy {
                override fun onConfigureTab(tab: TabLayout.Tab, position: Int) {
                    var item = pageModelList[position]
                    tab.setText(item.textId)
                    tab.setIcon(item.iconId)
                }

            }).attach()
        binding.mainBottomTabLayout.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
//                var f = FuckDialogConfig()
//                f.isEnable = true;
//                f.keyword = "wangwang"
//                ConfigUtil.setFuckDialogConfig(packageName, f)
//                LogUtil.d(">>>", f)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })
//      自行实现绑定：
//        binding.mainContentViewPager.registerOnPageChangeCallback(object :
//            ViewPager2.OnPageChangeCallback() {
//            override fun onPageSelected(position: Int) {
//                super.onPageSelected(position)
//                var item = pageModelList[position]
//                binding.mainBottomTabLayout.getTabAt(position)?.apply {
//                    setText(item.textId)
//                    setIcon(item.iconId)
//                    select()
//                }
//            }
//        })
//        binding.mainBottomTabLayout.addOnTabSelectedListener(object :
//            TabLayout.OnTabSelectedListener {
//            override fun onTabSelected(tab: TabLayout.Tab?) {
//                var position: Int = tab?.position ?: 0
//                binding.mainContentViewPager.setCurrentItem(position, true)
//            }
//
//            override fun onTabUnselected(tab: TabLayout.Tab?) {
//
//            }
//
//            override fun onTabReselected(tab: TabLayout.Tab?) {
//
//            }
//        })

    }


    private class PageAdapter(var activity: MainActivity, var viewModelList: List<PageModel>) :
        FragmentStateAdapter(activity) {
        override fun getItemCount(): Int {
            return viewModelList.size
        }

        override fun createFragment(position: Int): Fragment {
            return viewModelList[position].createFragment()
        }
    }

}