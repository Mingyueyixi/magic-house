package com.lu.magic.util

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo


class PackageUtil {
    companion object {
        fun getInstallPackageNameListByShell(): List<String> {
            var result = CmdUtil.exec("pm list package")
            var lines = result.success.split("\n")

            return lines.map { it ->
                it.trim().replace("package:", "")
            }
        }

        fun getInstallPackageInfoList(context: Context): MutableList<PackageInfo> {
            val pm = context.packageManager
            return pm.getInstalledPackages(0)
        }

        fun isSystemApp(pkg: PackageInfo): Boolean {
            return hasFlag(pkg.applicationInfo, ApplicationInfo.FLAG_SYSTEM)
        }

        fun isDebugApp(pkg: PackageInfo): Boolean {
            return hasFlag(pkg.applicationInfo, ApplicationInfo.FLAG_DEBUGGABLE)
        }

        fun hasFlag(appInfo: ApplicationInfo, flag: Int): Boolean {
            //二进制位与。原先|设置
            return appInfo.flags and flag === flag
        }
    }
}
