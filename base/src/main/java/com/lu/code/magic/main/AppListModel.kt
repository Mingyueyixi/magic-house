package com.lu.code.magic.main

import android.content.pm.PackageInfo
import android.graphics.drawable.Drawable

class AppListModel(
    var packageInfo: PackageInfo,
    var name: String,
    var packageName: String,
    var icon: Drawable?,
    var enable: Boolean
)