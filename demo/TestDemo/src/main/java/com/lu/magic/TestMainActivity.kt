package com.lu.magic

import android.os.Bundle
import android.view.View
import com.blankj.utilcode.util.VibrateUtils
import com.lu.magic.config.ConfigUtil
import com.lu.magic.testdemo.databinding.LayoutMainBinding
import com.lu.magic.ui.BaseActivity
import com.lu.magic.util.GsonUtil
import com.lu.magic.util.ToastUtil

class TestMainActivity : BaseActivity() {
    private var binding: LayoutMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ConfigUtil.init(this)
        binding = LayoutMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        binding!!.btnClickVibrate.setOnClickListener { v: View? -> VibrateUtils.vibrate(75L) }
    }

    override fun onResume() {
        super.onResume()
        val data = ConfigUtil.getAll()?: mapOf()
        ToastUtil.show(GsonUtil.toJson(data))

    }
}