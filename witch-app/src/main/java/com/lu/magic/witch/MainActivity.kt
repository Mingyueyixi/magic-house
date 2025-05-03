package com.lu.magic.witch

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.lu.magic.witchapp.R
import com.lu.magic.bridge.BridgeConstant
import com.lu.magic.ui.BaseActivity
import com.lu.magic.ui.FragmentNavigation
import com.lu.magic.util.log.LogUtil
import com.lu.magic.witchapp.databinding.LayoutFragmentContainerBinding

class MainActivity : BaseActivity() {
    private lateinit var mFragmentNavigation: FragmentNavigation
    private lateinit var mBinding: LayoutFragmentContainerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = LayoutFragmentContainerBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        mBinding.toolbarLayout.visibility = View.VISIBLE
        mBinding.toolbar.navigationIcon = null
        setSupportActionBar(mBinding.toolbar)
        //设置导航点击监听，在setSupportActionBar之后，否则无效
        mBinding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        mFragmentNavigation = FragmentNavigation(this, findViewById(R.id.fragment_container))
        mFragmentNavigation.navigate(MainFragment::class.java)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        LogUtil.i("requestCode:", requestCode, "resultCode:", resultCode)
        if (requestCode == BridgeConstant.CODE_ACTIVITY_REQUEST_CODE) {
            val json = data?.getStringExtra(BridgeConstant.CODE_INTENT_RESULT_DATA)
            LogUtil.i("result data:", json)
        }
    }

}
