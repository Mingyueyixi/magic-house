package com.lu.magic

import androidx.annotation.Keep

@Keep
class AppInfo(
    versionCode: Int = BuildConfig.VERSION_CODE,
    versionName: String = BuildConfig.VERSION_NAME,
    buildType: String = BuildConfig.BUILD_TYPE,
    applicationId: String = BuildConfig.APPLICATION_ID
)