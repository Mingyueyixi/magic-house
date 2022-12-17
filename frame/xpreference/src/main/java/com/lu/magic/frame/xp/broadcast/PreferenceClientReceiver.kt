package com.lu.magic.frame.xp.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * 偏好设置服务广播接收器
 */
class PreferenceClientReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val requestId = intent?.getStringExtra("requestId")
        val func = ClientSession.onReceivePool.remove(requestId)
        func?.onReceive(intent)
    }

}

