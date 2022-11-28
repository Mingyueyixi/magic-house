package com.lu.magic.frame.xp.util.log

import android.util.Log
import java.util.Arrays

internal object XPLogUtil {
    @JvmField
    var logger: IXPLog = DefaultLogger()

    fun setLogger(log: IXPLog) {
        logger = log
    }

    @JvmStatic
    fun d(vararg objects: Any?) {
        logger.d(objects)
    }

    @JvmStatic
    fun i(vararg objects: Any?) {
        logger.i(objects)
    }

    @JvmStatic
    fun w(vararg objects: Any?) {
        logger.w(objects)
    }

    @JvmStatic
    fun e(vararg objects: Any?) {
        logger.e(objects)
    }

    @JvmStatic
    fun buildLogText(vararg objects: Any?): String {
        val text = StringBuffer()
        objects.forEachIndexed { index, obj ->
            val objText = when (obj) {
                is Throwable -> Log.getStackTraceString(obj)
                is Array<*> -> Arrays.deepToString(obj)
                else -> obj.toString()
            }
            if (index == 0) {
                text.append(objText)
            } else {
                text.append("  $objText")
            }
        }
        return text.toString()
    }
}