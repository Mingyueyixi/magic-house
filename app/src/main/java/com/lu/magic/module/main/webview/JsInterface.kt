package com.lu.magic.module.main.webview

interface JsInterface {
    fun getAppInfo(): String = ""
    fun goBack() = Unit
    fun openUri(uri:String?) = Unit
}