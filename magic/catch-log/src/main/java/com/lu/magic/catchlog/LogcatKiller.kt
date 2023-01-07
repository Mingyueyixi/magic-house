package com.lu.magic.catchlog

import android.os.Build
import com.lu.magic.util.sh.ContinuousShell
import com.lu.magic.util.AppUtil
import com.lu.magic.util.IOUtil
import com.lu.magic.util.log.LogUtil
import kotlinx.coroutines.*
import java.io.File

class LogcatKiller {
    companion object {

        fun kill(process: Process?) {
            process?.let {
                IOUtil.writeByString("exit", it.outputStream)
                it.outputStream.flush()
                IOUtil.closeQuietly(it.inputStream, it.errorStream, it.outputStream)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    it.destroyForcibly()
                } else {
                    it.destroy()
                }
                it.waitFor()
            }
            GlobalScope.launch {
                withContext(Dispatchers.IO) {
                    val shell = ContinuousShell.open(true)
                    for (i in 1..3) {
                        try {
                            //关一次不一定完成，可能不成功或者进程被重新拉起
                            exitLogcatShell(shell)
                            delay(500L * i)
                        } catch (e: Exception) {
                            LogUtil.e(e)
                        }
                    }
                    IOUtil.closeQuietly(shell)

                }

            }
        }


        private fun exitLogcatShell(shell: ContinuousShell) {
            val logcatProcessPath = "${AppUtil.getContext().externalCacheDir}/logcat_info.txt"
            val logInfoFile = File(logcatProcessPath)
            runCatching {
                if (logInfoFile.exists()) {
                    logInfoFile.delete()
                }
            }
            // 从输入流读取会卡住，管道>写入文件后读取
            val result = shell.exec(arrayOf("ps -A | grep logcat"), true)
            LogUtil.i("cmd--ps--> $result")
            val cmdArray = result.success.split("\n").map {
                val m = Regex("\\s\\d+\\s").find(it)
                if (m == null) {
                    ""
                } else {
                    "kill ${m.value.trim()}"
                }
            }.toTypedArray()
            LogUtil.i("will exec cmd", cmdArray.joinToString())
            shell.exec(cmdArray, true)
        }


    }

}