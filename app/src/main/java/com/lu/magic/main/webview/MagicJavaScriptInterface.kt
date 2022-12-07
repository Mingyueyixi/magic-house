package com.lu.magic.main.webview

import android.webkit.JavascriptInterface
import com.lu.magic.AppInfo

class MagicJavaScriptInterface {

    @JavascriptInterface
    fun getAppInfo(): AppInfo {
        return AppInfo()
    }


}