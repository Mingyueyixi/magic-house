package com.lu.magic.main

import android.content.Context
import android.content.Intent
import com.lu.magic.store.ItemModel
import com.lu.magic.util.SingleClassStoreUtil

object AppRouter {

    fun routeDetailConfigPage(
        context: Context,
        routeItem: ItemModel,
        itemData: AppListModel?
    ) {
        SingleClassStoreUtil.put(itemData)
        SingleClassStoreUtil.put(routeItem)
        val intent = Intent(context, DetailConfigActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

}