package com.lu.magic.util.sh

import android.os.Build
import com.lu.magic.util.AppUtil
import com.lu.magic.util.CmdUtil
import com.lu.magic.util.IOUtil
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile
import java.nio.channels.FileChannel
import java.nio.channels.FileLock

/**
 * 命令工具类，for android
 * 可以在一个进程中执行多次命令的工具
 */
class ContinuousShell(val process: Process, val root: Boolean) : CmdUtil.Shell {
    companion object {
        fun open(root: Boolean): ContinuousShell {
            val process = Runtime.getRuntime().exec(if (root) "su" else "sh")
            return ContinuousShell(process, root)
        }
    }

    override fun exec(commands: Array<out String?>?, isNeedResult: Boolean): CmdUtil.Result {
        if (commands == null) {
            return CmdUtil.Result(-1, "", "", null)
        }
        if (isNeedResult) {
            return withResultImpl(commands)
        }
        return notResultImpl(commands)
    }

    /**
     * 当需要结果时，将自动给命令追加 > xxx.txt ，
     * 这是不稳定的操作，
     *
     */
    private fun withResultImpl(commands: Array<out String?>): CmdUtil.Result {
        val outputStream = process.outputStream
        val resultPath = "${AppUtil.getContext().externalCacheDir}/sh_exec_result.txt"
        commands.forEachIndexed { index, s ->
            if (!s.isNullOrEmpty()) {
                val cmd = if (!s.contains(">")) {
                    if (index == 0) {
                        //重头写入
                        "$s > $resultPath \n"
                    } else {
                        //续写
                        "$s >> $resultPath \n"
                    }
                } else {
                    "$s\n"
                }
                IOUtil.writeByString(cmd, outputStream)
                outputStream.flush()
            }
        }
        val resultFile = File(resultPath)
        while (true) {
            if (resultFile.exists()) {
                break
            }
        }

        var fileLock: FileLock? = null
        var randomAccessFile: RandomAccessFile? = null
        var fileChannel: FileChannel? = null
        var resultText: String? = null
        try {
            randomAccessFile = RandomAccessFile(resultPath, "rw")
            fileChannel = randomAccessFile.channel
            fileLock = fileChannel.lock()
            val buff = ByteArray(8192)
            val bao = ByteArrayOutputStream()
            var len: Int
            while (true) {
                len = randomAccessFile.read(buff)
                if (len <= -1) {
                    break
                }
                bao.write(buff, 0, len)
                bao.flush()
            }
            resultText = bao.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            resultText = resultFile.readText()
        } finally {
            runCatching {
                if (fileLock != null && fileLock.isValid) {
                    fileLock.release()
                }
                IOUtil.closeQuietly(fileChannel, randomAccessFile)
            }
        }
        return CmdUtil.Result(-1, resultText ?: "", "", commands)
    }


    private fun notResultImpl(commands: Array<out String?>): CmdUtil.Result {
        val outputStream = process.outputStream
        for (cmd in commands) {
            IOUtil.writeByString("$cmd\n", outputStream)
            outputStream.flush()
        }
//        if (isNeedResult) {
//            //process未退出无法读取，会阻塞线程
//            val success = IOUtil.readToString(process.inputStream)
//            val error = IOUtil.readToString(process.errorStream)
//            return CmdUtil.Result(-1, success, error, commands)
//        }
        return CmdUtil.Result(-1, "", "", commands)
    }

    override fun close() {
        runCatching {
            IOUtil.writeByString("exit\n", process.outputStream)
            process.outputStream.flush()
            process.waitFor()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                process.destroyForcibly()
            } else {
                process.destroy()
            }
            IOUtil.closeQuietly(process.outputStream, process.inputStream, process.errorStream)
        }.onFailure {
            throw IOException(it)
        }

    }
}