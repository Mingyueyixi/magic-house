package com.lu.magic.main.webview

import android.R
import android.content.Intent
import android.net.Uri
import android.webkit.JavascriptInterface
import androidx.core.content.ContextCompat
import com.lu.magic.bean.AppInfo
import com.lu.magic.bean.BuildInfo
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
                R.attr.colorPrimary,
                R.attr.colorPrimaryDark,
            )
        )
        val colorPrimary = ContextCompat.getColor(context, com.lu.magic.base.R.color.purple_200)
        val colorPrimaryDark = ContextCompat.getColor(context, com.lu.magic.base.R.color.teal_200)
        val appInfo = _root_ide_package_.com.lu.magic.bean.AppInfo(
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