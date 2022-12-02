package com.lu.magic.util

import android.os.Build
import com.lu.magic.util.log.LogUtil
import java.io.Closeable
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

object CmdUtil {
    private const val LINE_SEP = "\n"

    @JvmStatic
    fun run(vararg commands: String?) {
        if (NullUtil.isAllNull(commands)) {
            return
        }
        OnceShell.open(false).exec(commands, false)
    }

    @JvmStatic
    fun exec(vararg command: String?): Result {
        return exec(arrayOf(*command), false)
    }

    @JvmStatic
    fun exec(commands: Array<String?>?, root: Boolean = false): Result {
        return OnceShell.open(root).exec(commands, true)
    }

    interface Shell : Closeable {
        fun exec(commands: Array<out String?>?, isNeedResult: Boolean): Result
    }

// 暂时无法实现只打开一个进程，完成执行一次命令，读取一次结果，不关闭进程读取会在读取一行后卡住

    class OnceShell private constructor(val process: Process, val root: Boolean) : Shell {
        companion object {
            @JvmStatic
            fun open(root: Boolean = false): Shell {
                val process = Runtime.getRuntime().exec(if (root) "su" else "sh")
                return OnceShell(process, root)
            }
        }

        /**
         * 执行一次即关闭
         */
        override fun exec(commands: Array<out String?>?, isNeedResult: Boolean): Result {
            var pExitCode = -1
            if (commands.isNullOrEmpty() || NullUtil.isAllNull(commands)) {
                return Result(pExitCode, "", "", commands)
            }
            var successMsg: String? = null
            var errorMsg: String? = null
            var outputStream = process.outputStream
            var successStream: InputStream? = null
            var errorStream: InputStream? = null

            try {
                for (command in commands) {
                    if (command == null) continue
                    IOUtil.writeByString("\n$command\n", outputStream)
                    outputStream?.flush()
                }
                //退出
                IOUtil.writeByString("exit\n", outputStream)
                outputStream.flush()

                //等待结果
                pExitCode = process.waitFor()
                if (isNeedResult) {
                    successStream = process.inputStream
                    errorStream = process.errorStream
                    successMsg = IOUtil.readToString(successStream)
                    errorMsg = IOUtil.readToString(errorStream)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                IOUtil.closeQuietly(outputStream, successStream, errorStream)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    process.destroyForcibly()
                } else {
                    process.destroy()
                }
            }
            return Result(pExitCode, successMsg ?: "", errorMsg ?: "", commands)
        }

        override fun close() {
            //empty
        }

    }

    class Result(var code: Int, var success: String, var error: String, var commands: Array<out String?>?) {
        override fun toString(): String {
            return """CmdUtil.Result: {
    "code": $code,
    "successMsg": "$success",
    "errorMsg": "$error",
    "commands": "${commands.contentToString()}"
}""".trimMargin()
        }
    }
}