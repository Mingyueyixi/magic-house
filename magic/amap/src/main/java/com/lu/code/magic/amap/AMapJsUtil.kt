package com.lu.code.magic.amap

import android.content.Context
import android.webkit.WebView
import com.lu.code.magic.util.GsonUtil
import com.lu.code.magic.util.IOUtil
import java.io.InputStream

class H5AMapBridge {
    lateinit var jsCode: String;
    fun getJsCodeText(context: Context): String {
        var iStream: InputStream = context.assets.open("get_amap_location.js")
        var text = IOUtil.readToString(iStream)
        IOUtil.closeQuietly(iStream)
        return text
    }

    fun getSelectLocation(webView: WebView, callback: LocationValueCallBack) {
        webView.evaluateJavascript(jsCode) {
            var hasError = false;
            if (it == null) {
                hasError = true
            }
            var value = GsonUtil.fromJson<H5SelectLocation>(it, H5SelectLocation::class.java)
            if (value.error.isNotEmpty()) {
                hasError = true
            }

            if (hasError) {
                callback.onReceiveValue(null, null)
                return@evaluateJavascript
            }

            var lot = value.lon.toFloatOrNull()
            var lat = value.lat.toFloatOrNull()
            callback.onReceiveValue(lot, lat)
        }
    }

    interface LocationValueCallBack {
        fun onReceiveValue(v1: Float?, v2: Float?)
    }
}

class H5SelectLocation(var lon: String, var lat: String, var error: String)
