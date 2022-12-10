package com.lu.magic.util.log

import android.util.Log

open class SimpleLogger : ILogger {
    companion object {
        const val TAG = ">>>"
    }

    override fun v(vararg objects: Any?) {
        onLog(0, objects)
    }

    override fun d(vararg objects: Any?) {
        onLog(1, objects)
    }

    override fun i(vararg objects: Any?) {
        onLog(2, objects)
    }

    override fun w(vararg objects: Any?) {
        onLog(3, objects)
    }

    override fun e(vararg objects: Any?) {
        onLog(4, objects)
    }

    open fun beforeLog(level: Int, objects: Array<out Any?>, msg: String) {

    }

    open fun onLog(level: Int, objects: Array<out Any?>) {
        val msg = buildLogText(objects)
        beforeLog(level, objects, msg)
        when (level) {
            0 -> Log.v(TAG, msg)
            1 -> Log.d(TAG, msg)
            2 -> Log.i(TAG, msg)
            3 -> Log.w(TAG, msg)
            4 -> Log.e(TAG, msg)
            else -> Log.d(TAG, msg)
        }
        afterLog(level, objects, msg)
    }

    open fun afterLog(level: Int, objects: Array<out Any?>, msg: String) {

    }

    open fun buildLogText(objects: Array<out Any?>): String {
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