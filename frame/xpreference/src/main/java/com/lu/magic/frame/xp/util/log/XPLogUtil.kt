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
        logger.d(*objects)
    }

    @JvmStatic
    fun i(vararg objects: Any?) {
        logger.i(*objects)
    }

    @JvmStatic
    fun w(vararg objects: Any?) {
        logger.w(*objects)
    }

    @JvmStatic
    fun e(vararg objects: Any?) {
        logger.e(*objects)
    }

    @JvmStatic
    fun buildLogText(objects: Array<out Any?>): String {
        val text = StringBuffer()
        objects.forEachIndexed { i, obj ->
            if (i != 0) {
                text.append("  ")
            }
            if (obj == null) {
                text.append("null")
            } else {
                if (obj is Throwable) {
                    text.append(Log.getStackTraceString(obj))
                } else if (obj.javaClass.isArray) {
                    text.append((obj as Array<*>).contentToString())
                } else {
                    text.append(obj)
                }
            }
        }
        return text.toString()
    }
}