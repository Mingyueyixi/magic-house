package com.lu.magic.util.log

interface ILogger {
    fun v(vararg objects: Any?)
    fun d(vararg objects: Any?)
    fun i(vararg objects: Any?)
    fun w(vararg objects: Any?)
    fun e(vararg objects: Any?)
    fun log(vararg objects: Any?) = i(objects)
}