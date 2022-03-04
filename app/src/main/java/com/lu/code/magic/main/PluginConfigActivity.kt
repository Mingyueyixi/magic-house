package com.lu.code.magic.main;

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.fragment.app.Fragment
import com.lu.code.magic.magic.databinding.ActivityPluginConfigBinding
import com.lu.code.magic.main.store.ItemModel
import com.lu.code.magic.ui.BaseActivity

class PluginConfigActivity : BaseActivity() {
    private lateinit var binding: ActivityPluginConfigBinding
    private lateinit var routeItem: ItemModel

    companion object {
        private const val KEY_ROUTE_ITEM = "KEY_ROUTE_ITEM"

        fun start(context: Context, item: ItemModel) {
            if (item.page.pageCls == null) {
                return
            }
            val intent = Intent(context, PluginConfigActivity::class.java)
            intent.putExtra(KEY_ROUTE_ITEM, item)
            context.startActivity(intent)
        }
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = ActivityPluginConfigBinding.inflate(layoutInflater)
        setContentView(binding.root)

        routeItem = intent.getSerializableExtra(KEY_ROUTE_ITEM) as ItemModel
        if (routeItem == null) {
            finish()
            return
        }

        if (routeItem.page.pageCls == null) {
            return
        }

        initToolBar()
        attachPage()
    }

    private fun initToolBar() {
        binding.toolbar.title = routeItem.page.title
        //在设置setSupportActionBar之前设置toolbar标题，否则无效
        setSupportActionBar(binding.toolbar)
        //设置导航点击监听，在setSupportActionBar之后，否则无效
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }


    private fun attachPage() {
        var fragment: Fragment = (routeItem.page.pageCls as Class<Fragment>).newInstance()
        supportFragmentManager.beginTransaction().also {
            it.replace(binding.fragmentContainer.id, fragment, routeItem.page.pageCls.toString())
            it.commit()
        }

    }

}


