package com.lu.magic.frame.xp.socket

import com.lu.magic.frame.xp.bean.ContractRequest
import com.lu.magic.frame.xp.bean.ContractResponse
import com.lu.magic.frame.xp.util.KxGson
import com.lu.magic.frame.xp.util.log.XPLogUtil
import java.net.Socket


class PreferencesSocketClient(val host: String = "127.0.0.1", val port: Int) {
    private var clientThread: Thread? = null
    private var requestCallBackPool: MutableMap<String, CallBack?> = mutableMapOf()

    fun request(request: ContractRequest, callBack: CallBack) {
        if (clientThread?.isAlive == true) {
            return
        }
        clientThread = Thread {
            runCatching {
                val socket = Socket(host, port)
                Session(this, socket).handle(request)
            }.onFailure {
                try {
                    XPLogUtil.e(it)
                    val eText = ContractResponse(null, it).let {
                        KxGson.GSON.toJson(it)
                    }
                    requestCallBackPool.remove(request.requestId)?.onResponse(eText)
                } catch (e: Exception) {
                    XPLogUtil.e(e)
                }
            }
        }
        requestCallBackPool[request.requestId] = callBack
        clientThread?.start()
    }

    class Session(val client: PreferencesSocketClient, override val socket: Socket) : BaseSession(socket) {

        fun handle(request: ContractRequest) {
            initStreamIf()
            runCatching {
                val json = KxGson.GSON.toJson(request)
                sendToServer(json)
            }
            runCatching {
                bReader?.let {
                    var line: String? = null
                    while (true) {
                        line = it.readLine()
                        if (line != null) {
                            break
                        }
                    }
                    val resp = KxGson.GSON.fromJson(line, ContractResponse::class.java)
                    val func = client.requestCallBackPool.remove(resp.responseId)
                    func?.onResponse(line)
                }
            }
        }

        private fun sendToServer(text: String) {
            bWriter?.let {
                it.write(text)
                it.newLine()
                it.flush()
            }
        }
    }

    fun interface CallBack {
        fun onResponse(text: String?)
    }

    fun interface CallBack2<T> {
        fun onResponse(response: ContractResponse<T>)
    }

}