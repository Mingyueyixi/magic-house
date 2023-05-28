package com.lu.magic.module.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.lu.magic.module.R
import com.lu.magic.module.databinding.ActivityMainBinding
import com.lu.magic.module.main.vm.MainViewModel
import com.lu.magic.ui.BaseActivity
import com.lu.magic.util.FragmentUtil
import com.lu.magic.util.dialog.DialogUtil


class MainActivity : BaseActivity() {
    private lateinit var pageModelList: ArrayList<PageModel>
    private lateinit var binding: ActivityMainBinding
    private val vm by lazy { ViewModelProvider(this)[MainViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pageModelList = arrayListOf(
            PageModel(R.drawable.ic_icon_store, R.string.store) {
                FragmentUtil.newInstance<StoreFragment>()
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

        TabLayoutMediator(binding.mainBottomTabLayout, binding.mainContentViewPager, true) { tab, position ->
            val item = pageModelList[position]
            tab.setText(item.textId)
            tab.setIcon(item.iconId)
        }.attach()

        binding.mainBottomTabLayout.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
//                var f = FuckDialogConfig()
//                f.isEnable = true;
//                f.keyword = "wangwang"
//                LogUtil.d(">>>", ConfigUtil.getFuckDialogConfigAll())
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

        vm.checkShowAgreement().observe(this) {
            DialogUtil.buildAlertDialog(this)
                .setMessage(it)
                .setNegativeButton(com.lu.magic.base.R.string.cancel_and_exit) { dialog, _ ->
                    finishAfterTransition()
                }
                .setPositiveButton(com.lu.magic.base.R.string.agree_and_continue) { dialog, _ ->
                    vm.saveHasAgreeAgreement()
                }
                .setCancelable(false)
                .show()
        }
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