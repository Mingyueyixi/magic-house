package com.lu.magic.frame.xp.socket

import android.content.Context
import com.lu.magic.frame.xp.bean.ContractRequest
import com.lu.magic.frame.xp.bean.ContractResponse
import com.lu.magic.frame.xp.util.KxGson
import com.lu.magic.frame.xp.util.log.XPLogUtil
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class RequestTransfer(val port: Int) {

    fun <T> request(context: Context, request: ContractRequest, rClass: Class<T>, listener: OnResponseListener<T>?) {
        PreferencesSocketClient(port = port).request(request) {
            val ty = KxGson.getType(ContractResponse::class.java, rClass)
            val resp: ContractResponse<T?> = KxGson.GSON.fromJson(it, ty)
            listener?.onResponse(request, resp)
        }
    }

    fun <T> requestWithBlock(context: Context, request: ContractRequest, rClass: Class<T>): ContractResponse<T?> {
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
            XPLogUtil.w("Request Session is TimeOut for await")
            response = ContractResponse(null, TimeoutException("wait response timeout!"))
        }
        return response ?: ContractResponse(null, null)
    }


    fun interface OnResponseListener<T> {
        fun onResponse(req: ContractRequest, resp: ContractResponse<T?>?)
    }
}