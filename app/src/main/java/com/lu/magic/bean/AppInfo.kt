package com.lu.magic.bean

import androidx.annotation.Keep
import com.lu.magic.BuildConfig
import com.lu.magic.R
import com.lu.magic.util.AppUtil

@Keep
class AppInfo(
    var versionCode: Int = BuildConfig.VERSION_CODE,
    var versionName: String = BuildConfig.VERSION_NAME,
    var buildType: String = BuildConfig.BUILD_TYPE,
    var applicationId: String = BuildConfig.APPLICATION_ID,
    var appName: String = AppUtil.getContext().getString(R.string.app_name),
    var colorPrimary: String,
    var colorPrimaryDark:String,
    var buildInfo:BuildInfo
)