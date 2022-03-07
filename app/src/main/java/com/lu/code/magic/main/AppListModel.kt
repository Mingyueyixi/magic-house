package com.lu.code.magic.main

import android.graphics.drawable.Drawable

class AppListModel(
    var name: String,
    var packageName: String,
    var icon: Drawable?,
    var enable: Boolean
) {
    constructor() : this("", "", null, false)
    constructor(name: String, packageName: String, enable: Boolean) : this(
        name,
        packageName,
        null,
        enable
    )
}