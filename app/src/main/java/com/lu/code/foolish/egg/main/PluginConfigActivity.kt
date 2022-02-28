package com.lu.code.foolish.egg.main;

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.lu.code.foolish.egg.databinding.ActivityPluginConfigBinding
import com.lu.code.foolish.egg.ui.BaseActivity

class PluginConfigActivity : BaseActivity() {
    private lateinit var binding: ActivityPluginConfigBinding
    private lateinit var fragmentCls: Class<out Fragment>

    companion object {
        private const val KEY_FRAGMENT_CLS = "fragment-cls"

        fun start(context: Context, fragmentCls: Class<out Fragment>) {
            val intent = Intent(context, PluginConfigActivity.javaClass)
            intent.putExtra(KEY_FRAGMENT_CLS, fragmentCls)
            context.startActivity(intent)
        }
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPluginConfigBinding.inflate(layoutInflater)
        var fragmentCls = intent.getSerializableExtra(KEY_FRAGMENT_CLS)
        if (fragmentCls == null) {
            finish()
            return
        }
        var fragment: Fragment = fragmentCls.javaClass.newInstance() as Fragment
        supportFragmentManager.beginTransaction().also {
            it.replace(binding.fragmentContainer.id, fragment, fragmentCls.toString())
        }
    }
}

