package com.lu.magic.main

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.lu.magic.bridge.BridgeConstant
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

    fun routeWitchApp(activity: Activity, routeItem: ItemModel, itemData: AppListModel) {
        //跳转到辅助APP
        val intent = Intent()
//        不能添加FLAG_ACTIVITY_NEW_TASK，否则Activity将接收不到结果
//        但是不添加，在6.0一下一些手机中，将无法打开Activity！！！
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME)
        intent.setAction(BridgeConstant.CODE_MULTI_ACTIVITY_ACTION)
        intent.component = ComponentName("com.lu.magic", "com.lu.magic.witch.MultipleActivity")

        intent.putExtra(BridgeConstant.CODE_INTENT_MULTIPLE_PAGE, "amap")
        intent.putExtra(BridgeConstant.CODE_INTENT_REQUEST_DATA, "{}")

        activity.startActivityForResult(intent, BridgeConstant.CODE_ACTIVITY_REQUEST_CODE)

    }

}