package com.lu.magic.frame.xp.util

import com.lu.magic.frame.xp.util.log.XPLogUtil
import java.io.Closeable
import java.io.IOException

object IOUtil {
    @JvmStatic
    fun closeQuietly(vararg closeables: Closeable?) {
        for (closeable in closeables) {
            if (closeable == null) {
                continue
            }
            try {
                closeable.close()
            } catch (e: IOException) {
                try {
                    closeable.close()
                } catch (e: Exception) {
                    XPLogUtil.e(e)
                }
            }
        }
    }
}