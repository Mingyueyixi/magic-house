package com.lu.magic

import android.os.Bundle
import android.view.View
import com.blankj.utilcode.util.VibrateUtils
import com.lu.magic.config.ConfigUtil
import com.lu.magic.frame.xp.annotation.PreferenceIdValue
import com.lu.magic.frame.xp.BRPreference
import com.lu.magic.frame.xp.broadcast.BRConfig
import com.lu.magic.testdemo.databinding.LayoutMainBinding
import com.lu.magic.ui.BaseActivity
import com.lu.magic.util.AppUtil
import com.lu.magic.util.GsonUtil
import com.lu.magic.util.ToastUtil
import com.lu.magic.util.thread.AppExecutor

class TestMainActivity : BaseActivity() {
    private lateinit var sp: BRPreference
    private var binding: LayoutMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ConfigUtil.init(this)
        binding = LayoutMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        binding!!.btnClickVibrate.setOnClickListener { v: View? -> VibrateUtils.vibrate(75L) }

        sp = BRPreference(
            AppUtil.getContext(),
            "config",
            PreferenceIdValue.SP,
            BRConfig("com.lu.magic.server", "com.lu.magic.client")
        )
        BRPreference.registerAsClient(AppUtil.getContext(), sp.config)

    }

    override fun onResume() {
        super.onResume()
//        val data = ConfigUtil.getAll()?: mapOf()
//        ToastUtil.show(GsonUtil.toJson(data))


        AppExecutor.executeIO {
            val data = sp.all
            AppExecutor.executeMain {
                ToastUtil.show(GsonUtil.toJson(data))
            }
        }

    }
}