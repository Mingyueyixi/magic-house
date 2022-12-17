package com.lu.magic.frame.xp.util.log

import android.util.Log

class DefaultLogger : IXPLog {
    companion object {
        private const val TAG = ">>>xp"
    }

    override fun d(vararg objects: Any?) {
        Log.d(TAG, XPLogUtil.buildLogText(objects))
    }

    override fun i(vararg objects: Any?) {
        Log.i(TAG, XPLogUtil.buildLogText(objects))
    }

    override fun w(vararg objects: Any?) {
        Log.w(TAG, XPLogUtil.buildLogText(objects))
    }

    override fun e(vararg objects: Any?) {
        Log.e(TAG, XPLogUtil.buildLogText(objects))
    }


}