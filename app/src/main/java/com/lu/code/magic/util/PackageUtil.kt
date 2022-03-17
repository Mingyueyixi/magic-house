package com.lu.code.magic.util

import android.content.Context
import android.content.pm.PackageInfo


class PackageUtil {
    companion object {
        fun getInstallPackageNameListByShell(): List<String> {
            var result = CmdUtil.exec("pm list package")
            var lines = result.successMsg.split("\n")

            return lines.map { it ->
                it.trim().replace("package:", "")
            }
        }

        fun getInstallPackageInfoList(context: Context): MutableList<PackageInfo> {
            val pm = context.packageManager
            return pm.getInstalledPackages(0)
        }
    }
}