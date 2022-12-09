package com.lu.magic.bean

import android.util.Base64
import androidx.annotation.Keep
import com.lu.magic.BuildConfig
import java.io.ByteArrayInputStream
import java.util.*

@Keep
class BuildInfo(
    val buildMillis: Long,
    val gitCommit: String,
    val gitBranch: String,
) {
    companion object {
        val value by lazy {
            val decodeBin = Base64.decode(BuildConfig.BUILD_INFO_TEXT, Base64.DEFAULT)
            val p = Properties()
            ByteArrayInputStream(decodeBin).reader().use {
                p.load(it)
            }
            val buildMillis: String = p.getProperty("build.millis", "")
            val gitCommit: String = p.getProperty("git.commit", "")
            val gitBranch: String = p.getProperty("git.branch", "")
            return@lazy BuildInfo(buildMillis.toLongOrNull() ?: 0, gitCommit, gitBranch)
        }

    }
}