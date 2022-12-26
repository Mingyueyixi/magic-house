package com.lu.magic.frame.xp.socket


import android.content.Context
import android.util.Log
import com.google.gson.JsonObject
import com.lu.magic.frame.xp.PreferenceServerImpl
import com.lu.magic.frame.xp.bean.ContractRequest
import com.lu.magic.frame.xp.util.IOUtil
import com.lu.magic.frame.xp.util.KxGson
import com.lu.magic.frame.xp.util.log.XPLogUtil
import java.net.ServerSocket
import java.net.Socket


class PreferencesSocketServer(val context: Context, val port: Int, val timeout: Int = 5000) {
    private var serverThread: Thread? = null
    private val pref = PreferenceServerImpl(context)

    @Synchronized
    fun start() {
        if (serverThread?.isAlive == true) {
            return
        }
        serverThread = Thread {
            runCatching {
                val ss = ServerSocket(port)
                while (true) {
                    val socket = ss.accept()
                    socket.soTimeout = timeout
                    Session(pref, socket).handle()
                }
            }
        }
        serverThread?.start()
    }


    class Session(val pref: PreferenceServerImpl, override val socket: Socket) : BaseSession(socket) {

        fun handle() {
            initStreamIf()

            runCatching {
                var line: String? = null
                bReader?.let {
                    val lastMills = System.currentTimeMillis()
                    while (true) {
                        line = it.readLine()
                        if (line != null) {
                            break
                        }
                        val currMills = System.currentTimeMillis()
                        if (currMills - lastMills > socket.soTimeout) {
                            break
                        }
                    }
                }
                XPLogUtil.i(">>>socket client send: ", line)
                val request = KxGson.GSON.fromJson(line, ContractRequest::class.java)
                pref.call(request).let { KxGson.GSON.toJson(it) }
            }.onSuccess {
                sendToClient(it)
            }.onFailure {
                //read timeout or other exception
                val json = JsonObject().apply {
                    addProperty("exception", Log.getStackTraceString(it))
                }
                sendToClient(json.toString())
            }
            IOUtil.closeQuietly(bWriter, oWriter, oStream, bReader, iReader, iStream, socket)
        }

        private fun sendToClient(text: String) {
            XPLogUtil.i(">>>socket server send: ", text)
            bWriter?.let {
                it.write(text)
                it.newLine()
                it.flush()
            }
        }
    }

}