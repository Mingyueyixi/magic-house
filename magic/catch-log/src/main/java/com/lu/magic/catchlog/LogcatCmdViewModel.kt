package com.lu.magic.catchlog

import android.os.Build
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lu.magic.util.AppUtil
import com.lu.magic.util.CmdUtil
import com.lu.magic.util.IOUtil
import com.lu.magic.util.log.LogUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

class LogcatCmdViewModel : ViewModel() {
    @Volatile
    var alive = false

    @Volatile
    private var readingLog = false

    @Volatile
    var pauseReadLog = false


    val updateLogLive by lazy { MutableLiveData<Boolean>() }
    val cacheLines by lazy { CopyOnWriteArrayList<String>() }

    var logTextPath = ""
    var process: Process? = null

    fun genLogTextPath(): String {
        val format = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        logTextPath = "${AppUtil.getContext().externalCacheDir}/log_${
            format.format(Date())
        }.txt"
        return logTextPath
    }

    fun startLogcat() {
        viewModelScope.launch {
            kotlin.runCatching {
                Runtime.getRuntime().exec("su").let {
                    process = it
                    alive = true
                    withContext(Dispatchers.IO) {
                        IOUtil.writeByString("logcat > $logTextPath\n", it.outputStream)
                        val bufferWriter = it.outputStream.bufferedWriter()
                        bufferWriter.flush()
                    }
                }
            }.onFailure {
                LogUtil.e(it)
            }
        }
    }

    fun exitLogcatWithBlock() {
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
            process = null
        }
        var shell: CmdUtil.Shell? = null
        try {
            val result = CmdUtil.exec(arrayOf("ps -A | grep logcat"), true)
            LogUtil.i("cmd--ps--> $result")
            result.success.split("\n").map {
                val m = Regex("\\s\\d+\\s").find(it)
                if (m == null) {
                    ""
                } else {
                    "kill ${m.value.trim()}"
                }
            }.toList().let {
                LogUtil.i("will exec cmd", it)
                CmdUtil.exec(it.toTypedArray(), true)
            }
        } catch (e: Exception) {
            LogUtil.e(e)
        } finally {
            IOUtil.closeQuietly(shell)
        }

    }

    override fun onCleared() {
        super.onCleared()
        exitLogcatWithBlock()
        readingLog = false
    }

    fun startLoadLog() {
        if (readingLog) {
            return
        }
        viewModelScope.launch {
            readingLog = true
            withContext(Dispatchers.IO) {
                val iStream = File(logTextPath).inputStream()
                val iReader = InputStreamReader(iStream)
                val bufferReader = BufferedReader(iReader)
                var onFileEnd = false
                val preList = LinkedList<String>()
                var lastPostMills = System.currentTimeMillis()

                try {
                    while (readingLog) {
                        val line = bufferReader.readLine()
                        if (line.isNullOrBlank()) {
                            break
                        }
                        if (preList.size > 1080) {
                            preList.removeAt(0)
                        }
                        preList.add(line)
                    }
                    //第一次读取到文件末尾。通知更新
                    cacheLines.addAll(preList)
                    preList.clear()
                    updateLogLive.postValue(!(updateLogLive.value ?: true))
                    updateLogLive.postValue(!(updateLogLive.value ?: true))

                    while (readingLog) {
                        val line = bufferReader.readLine()
                        if (pauseReadLog) {
                            delay(500L)
                            continue
                        }
                        if (line.isNullOrBlank()) {
                            //Thread.sleep(2000)
                            delay(1000L)
                            continue
                        }
                        synchronized(cacheLines) {
                            if (cacheLines.size >= 1080L) {
                                cacheLines.removeAt(0)
                            }
                            cacheLines.add(line)
                        }
                        val currMills = System.currentTimeMillis()
                        if (currMills - lastPostMills > 100L) {
                            updateLogLive.postValue(!(updateLogLive.value ?: true))
                        }
                    }
                } catch (e: Throwable) {
                    LogUtil.e(e)
                } finally {
                    readingLog = false
                    IOUtil.closeQuietly(iStream, iReader, bufferReader)
                }
            }

        }
    }

}
