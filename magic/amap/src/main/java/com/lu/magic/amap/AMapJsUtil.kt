package com.lu.magic.amap

import android.content.Context
import android.webkit.WebView
import com.lu.magic.bean.JsonBean
import com.lu.magic.util.IOUtil
import com.lu.magic.util.JSONX
import org.json.JSONObject
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

            val value = H5SelectLocation.fromJson(it)
            if (value?.error?.isNotEmpty() == true) {
                hasError = true
            }
            if (hasError) {
                callback.onReceiveValue(null, null)
                return@evaluateJavascript
            }

            val lot = value?.lon?.toFloatOrNull()
            val lat = value?.lat?.toFloatOrNull()
            callback.onReceiveValue(lot, lat)
        }
    }

    interface LocationValueCallBack {
        fun onReceiveValue(v1: Float?, v2: Float?)
    }
}


class H5SelectLocation(var lon: String, var lat: String, var error: String) : JsonBean() {
    override fun toJson(): JSONObject {
        return JSONObject().apply {
            put("lon", lon)
            put("lat", lat)
            put("error", error)
        }
    }

    companion object {
        @JvmStatic
        fun fromJson(it: String?): H5SelectLocation? {
            if (it.isNullOrEmpty()) return null
            return try {
                val json = JSONObject(it)
                H5SelectLocation(
                    JSONX.optString(json, "lon"),
                    JSONX.optString(json, "lat"),
                    JSONX.optString(json, "error")
                )
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}
