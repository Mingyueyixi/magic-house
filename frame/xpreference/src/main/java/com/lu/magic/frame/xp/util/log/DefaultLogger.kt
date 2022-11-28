package com.lu.magic.frame.xp.util.log

import android.util.Log

class DefaultLogger : IXPLog {
    companion object {
        private const val TAG = ">>>xp"
    }

    override fun d(vararg objects: Any?) {
        Log.d(Companion.TAG, XPLogUtil.buildLogText(objects))
    }

    override fun i(vararg objects: Any?) {
        Log.i(Companion.TAG, XPLogUtil.buildLogText(objects))
    }

    override fun w(vararg objects: Any?) {
        Log.w(Companion.TAG, XPLogUtil.buildLogText(objects))
    }

    override fun e(vararg objects: Any?) {
        Log.e(Companion.TAG, XPLogUtil.buildLogText(objects))
    }


}