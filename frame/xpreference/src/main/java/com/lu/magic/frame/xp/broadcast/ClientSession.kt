package com.lu.magic.frame.xp.broadcast

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Looper
import androidx.core.os.HandlerCompat
import androidx.core.view.KeyEventDispatcher.Component
import com.lu.magic.frame.xp.bean.ContractRequest
import com.lu.magic.frame.xp.bean.ContractResponse
import com.lu.magic.frame.xp.util.KxGson
import com.lu.magic.frame.xp.util.log.XPLogUtil
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class ClientSession(val config: BRConfig) {
    companion object {
        @JvmStatic
        val onReceivePool = mutableMapOf<String, OnReceiverListener>()
        val excector = Executors.newCachedThreadPool()
    }

    private val mHandler by lazy { HandlerCompat.createAsync(Looper.getMainLooper()) }

    fun <T> request(
        context: Context, request: ContractRequest, rClass: Class<T>, listener: OnResponseListener<T>?
    ) {
        val intent = Intent()
        intent.action = config.serverAction
        intent.putExtra("request", KxGson.GSON.toJson(request))
        context.sendBroadcast(intent)

        addReceiverListener(request.requestId) { resIntent ->
            resIntent?.let {
                val data = it.getStringExtra("response")
                val ty = KxGson.getType(ContractResponse::class.java, rClass)
                val response: ContractResponse<T?> = KxGson.GSON.fromJson(data, ty)
                listener?.onResponse(request, response)
            }
        }

    }

    fun <T> requestWithBlock(
        context: Context, request: ContractRequest, rClass: Class<T>
    ): ContractResponse<T?> {
        if (Thread.currentThread() == Looper.getMainLooper().thread) {
            throw IllegalThreadStateException("main线程不能被阻塞，否则无法接收结果")
        }
        val latch = CountDownLatch(1)
        var response: ContractResponse<T?>? = null
        var isSuccessCallBack = false
        request(context, request, rClass) { req, resp ->
            response = resp
            isSuccessCallBack = true
            latch.countDown()
        }
        runCatching {
            latch.await(5L, TimeUnit.SECONDS)
        }.onFailure {
            XPLogUtil.w(it)
            response = ContractResponse(null, it)
        }
        if (!isSuccessCallBack) {
            XPLogUtil.w("is TimeOut for await")
            response = ContractResponse(null, TimeoutException("wait response timeout!"))
        }
        return response ?: ContractResponse(null, null)
    }

    private fun addReceiverListener(requestId: String, onReceiverListener: OnReceiverListener) {
        onReceivePool[requestId] = onReceiverListener
        mHandler.postDelayed({
            onReceivePool.remove(requestId)
        }, 5000L)
    }


    fun interface OnResponseListener<T> {
        fun onResponse(req: ContractRequest, resp: ContractResponse<T?>?)
    }

    fun interface OnReceiverListener {
        fun onReceive(resIntent: Intent?)
    }

}