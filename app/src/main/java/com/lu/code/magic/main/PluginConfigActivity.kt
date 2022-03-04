package com.lu.code.magic.main;

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
            if (item.pageCls == null) {
                return
            }
            val intent = Intent(context, PluginConfigActivity::class.java)
            intent.putExtra(KEY_ROUTE_ITEM, item)
            context.startActivity(intent)
        }
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPluginConfigBinding.inflate(layoutInflater)
        setContentView(binding.root)
        routeItem = intent.getSerializableExtra(KEY_ROUTE_ITEM) as ItemModel
        if (routeItem == null) {
            finish()
            return
        }
        if (routeItem.pageCls == null) {
            return
        }

        attachPage()
    }

    private fun attachPage() {
        var fragment: Fragment = (routeItem.pageCls as Class<Fragment>).newInstance()
        supportFragmentManager.beginTransaction().also {
            it.replace(binding.fragmentContainer.id, fragment, routeItem.pageCls.toString())
            it.commit()
        }
    }
}


