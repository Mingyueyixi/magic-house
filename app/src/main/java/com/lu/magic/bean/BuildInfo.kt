package com.lu.magic.bean

import android.util.Base64
import com.lu.magic.module.BuildConfig
import com.lu.magic.util.JSONX
import org.json.JSONObject
import java.io.ByteArrayInputStream
import java.util.Properties

class BuildInfo(
    val buildMillis: Long,
    val gitCommit: String,
    val gitBranch: String,
) : JsonBean() {
    override fun toJson(): JSONObject {
        return JSONObject().apply {
            put("buildMillis", buildMillis)
            put("gitCommit", gitCommit)
            put("gitBranch", gitBranch)
        }
    }

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

        @JvmStatic
        fun fromJson(it: String?): BuildInfo? {
            it ?: return null
            return try {
                val json = JSONObject(it)
                return BuildInfo(
                    JSONX.optLong(json, "buildMillis"),
                    JSONX.optString(json, "gitCommit"),
                    JSONX.optString(json, "gitBranch")
                )
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}