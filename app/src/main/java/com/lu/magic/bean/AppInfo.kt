package com.lu.magic.bean

import androidx.annotation.Keep
import com.lu.magic.module.BuildConfig
import com.lu.magic.module.R
import com.lu.magic.util.AppUtil
import com.lu.magic.util.JSONX
import org.json.JSONObject

@Keep
class AppInfo(
    var versionCode: Int? = BuildConfig.VERSION_CODE,
    var versionName: String? = BuildConfig.VERSION_NAME,
    var buildType: String? = BuildConfig.BUILD_TYPE,
    var applicationId: String? = BuildConfig.APPLICATION_ID,
    var appName: String? = AppUtil.getContext().getString(R.string.app_name),
    var colorPrimary: String?,
    var colorPrimaryDark: String?,
    var buildInfo: BuildInfo?
) : JsonBean() {
    companion object {
        @JvmStatic
        fun fromJson(it: String?): AppInfo? {
            it ?: return null
            return try {
                val json = JSONObject(it)
                AppInfo(
                    JSONX.optInt(json, "versionCode"),
                    JSONX.optString(json, "versionName"),
                    JSONX.optString(json, "buildType"),
                    JSONX.optString(json, "applicationId"),
                    JSONX.optString(json, "appName"),
                    JSONX.optString(json, "colorPrimary"),
                    JSONX.optString(json, "colorPrimaryDark"),
                    BuildInfo.fromJson(JSONX.optString(json, "buildInfo"))
                )
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    override fun toJson(): JSONObject {
        return JSONObject().apply {
            put("versionCode", versionCode)
            put("versionName", versionName)
            put("buildType", buildType)
            put("applicationId", applicationId)
            put("appName", appName)
            put("colorPrimary", colorPrimary)
            put("colorPrimaryDark", colorPrimaryDark)
            put("buildInfo", buildInfo?.toJson())
        }
    }
}