package com.lu.magic.witch

import android.content.Intent
import android.os.Bundle
import com.lu.magic.R
import com.lu.magic.bridge.BridgeConstant
import com.lu.magic.ui.BaseActivity
import com.lu.magic.ui.FragmentNavigation
import com.lu.magic.util.log.LogUtil

class MainActivity : BaseActivity() {
    private lateinit var mFragmentNavigation: FragmentNavigation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_fragment_container)
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
