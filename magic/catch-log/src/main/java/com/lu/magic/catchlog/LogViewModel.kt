package com.lu.magic.catchlog

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lu.magic.util.sh.ContinuousShell
import com.lu.magic.util.AppUtil
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

class LogViewModel : ViewModel() {
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
            withContext(Dispatchers.IO) {
                val shell = ContinuousShell.open(true)
                shell.exec(arrayOf("logcat > $logTextPath"), false)
                process = shell.process
                alive = true
            }
        }
    }

    fun destroyLogcat() {
        if (alive) {
            LogcatKiller.kill(process)
            alive = false
        }
    }

    override fun onCleared() {
        super.onCleared()
        destroyLogcat()
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
