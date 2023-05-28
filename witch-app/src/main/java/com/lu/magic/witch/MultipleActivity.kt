package com.lu.magic.witch

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.lu.magic.R
import com.lu.magic.databinding.LayoutFragmentContainerBinding
import com.lu.magic.ui.BaseToolBarActivity
import com.lu.magic.ui.FragmentNavigation
import com.lu.magic.witch.amap.FuckAMapFragment

class MultipleActivity : BaseToolBarActivity() {
    private lateinit var mFragmentNavigation: FragmentNavigation
    private lateinit var binding: LayoutFragmentContainerBinding

    override fun getToolBar(): Toolbar {
        return binding.toolbar
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutFragmentContainerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mFragmentNavigation = FragmentNavigation(this, findViewById(R.id.fragment_container))
        val page = intent.getStringExtra(com.lu.magic.bridge.BridgeConstant.CODE_INTENT_MULTIPLE_PAGE)
        if (page.isNullOrBlank()) {
            finish()
            return
        }
        when (page) {
            "amap" -> {
                mFragmentNavigation.navigate(FuckAMapFragment::class.java)
                binding.toolbar.title = "高德地图"
            }
        }

        initToolBar()
    }


    private fun initToolBar() {
        val toolbar = binding.toolbar
        binding.toolbarLayout.visibility = View.VISIBLE
        //在设置setSupportActionBar之前设置toolbar标题，否则无效
        setSupportActionBar(toolbar)
        //设置导航点击监听，在setSupportActionBar之后，否则无效
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

}