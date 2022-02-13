package com.lu.code.foolish.egg.main

import android.os.Bundle
import android.view.View
import androidx.core.util.Supplier
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.lu.code.foolish.egg.ui.BaseActivity
import com.lu.code.foolish.egg.R
import com.lu.code.foolish.egg.databinding.ActivityMainBinding
import com.lu.code.foolish.egg.util.FragmentUtil
import java.lang.reflect.Method
import java.util.ArrayList


class MainActivity : BaseActivity() {
    private lateinit var pageModelList: ArrayList<PageViewModel>
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        pageModelList = arrayListOf(
            PageViewModel(R.drawable.ic_icon_store, R.string.store) {
                FragmentUtil.newInstance<StoreFragment>()
            },
            PageViewModel(R.drawable.ic_icon_log, R.string.log) {
                FragmentUtil.newInstance<LogFragment>()
            },
            PageViewModel(R.drawable.ic_icon_about, R.string.about) {
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

        binding.mainContentViewPager.adapter = PageAdapter(this, pageModelList)

    }


    private class PageAdapter(var activity: MainActivity, var viewModelList: List<PageViewModel>) :
        FragmentStateAdapter(activity) {
        override fun getItemCount(): Int {
            return viewModelList.size
        }

        override fun createFragment(position: Int): Fragment {
            return viewModelList[position].createFragment()
        }

    }
}