package com.lu.magic.util.sh

import android.os.Build
import com.lu.magic.util.CmdUtil
import com.lu.magic.util.IOUtil
import com.lu.magic.util.NullUtil
import com.lu.magic.util.log.LogUtil
import java.io.InputStream


/**
 * 命令工具类，for android
 * 执行一次exec函数，即关闭命令进程。
 * 需一个进程执行多次命令，请参考：{@link ContinuousShell }
 */
class OnceShell private constructor(val process: Process, val root: Boolean) : CmdUtil.Shell {
    companion object {
        @JvmStatic
        fun open(root: Boolean = false): OnceShell {
            val process = Runtime.getRuntime().exec(if (root) "su" else "sh")
            return OnceShell(process, root)
        }
    }

    /**
     * 执行一次即关闭。注意，不能自动关闭那些本身就无限执行的命令（如：logcat > temp.txt），这类命令会阻塞线程，本身就需要强行关闭
     */
    override fun exec(commands: Array<out String?>?, isNeedResult: Boolean): CmdUtil.Result {
        var pExitCode = -1
        if (commands.isNullOrEmpty() || NullUtil.isAllNull(commands)) {
            return CmdUtil.Result(pExitCode, "", "", commands)
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
            runCatching {
                close()
            }.onFailure {
                LogUtil.e(it)
            }
        }
        return CmdUtil.Result(pExitCode, successMsg ?: "", errorMsg ?: "", commands)
    }

    override fun close() {
        IOUtil.closeQuietly(process.inputStream, process.errorStream, process.outputStream)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            process.destroyForcibly()
        } else {
            process.destroy()
        }
    }

}