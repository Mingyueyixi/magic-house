package com.lu.magic

import android.os.Bundle
import android.view.View
import com.blankj.utilcode.util.VibrateUtils
import com.lu.magic.config.ConfigUtil
import com.lu.magic.frame.xp.BRPreference
import com.lu.magic.testdemo.databinding.LayoutMainBinding
import com.lu.magic.ui.BaseActivity

class TestMainActivity : BaseActivity() {
    private lateinit var sp: BRPreference
    private var binding: LayoutMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ConfigUtil.initWithReadable(this)
        binding = LayoutMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        binding!!.btnClickVibrate.setOnClickListener { v: View? -> VibrateUtils.vibrate(75L) }

    }

    override fun onResume() {
        super.onResume()

    }

}