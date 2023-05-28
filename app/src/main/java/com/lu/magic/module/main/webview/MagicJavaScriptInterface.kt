package com.lu.magic.module.main.webview

import android.content.Intent
import android.net.Uri
import android.webkit.JavascriptInterface
import androidx.core.content.ContextCompat
import com.lu.magic.module.bean.AppInfo
import com.lu.magic.module.bean.BuildInfo
import com.lu.magic.util.AppUtil
import com.lu.magic.util.ColorUtil
import com.lu.magic.util.GsonUtil
import com.lu.magic.util.log.LogUtil

class MagicJavaScriptInterface(private val jsInterfaceProxy: JsInterface) : JsInterface {

    @JavascriptInterface
    override fun getAppInfo(): String {
        val context = AppUtil.getContext()
        val theme = context.theme
        val array = theme.obtainStyledAttributes(
            intArrayOf(
                android.R.attr.colorPrimary,
                android.R.attr.colorPrimaryDark,
            )
        )
        val colorPrimary = ContextCompat.getColor(context, com.lu.magic.base.R.color.purple_200)
        val colorPrimaryDark = ContextCompat.getColor(context, com.lu.magic.base.R.color.teal_200)
        val appInfo = AppInfo(
            colorPrimary = ColorUtil.toHTMLColor(colorPrimary),
            colorPrimaryDark = ColorUtil.toHTMLColor(colorPrimaryDark),
            buildInfo = BuildInfo.value
        )
        return GsonUtil.toJson(appInfo) ?: "{}"
    }

    @JavascriptInterface
    override fun goBack() {
        jsInterfaceProxy.goBack()
    }

    @JavascriptInterface
    override fun openUri(uri: String?) {
        if (uri.isNullOrEmpty()) {
            LogUtil.d("uri is empty")
            return
        }
        val intent = Intent()
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.data = Uri.parse(uri)
        AppUtil.getContext().startActivity(intent)
    }
}