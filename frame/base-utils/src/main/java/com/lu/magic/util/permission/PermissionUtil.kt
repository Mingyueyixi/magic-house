package com.lu.magic.util.permission

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.SparseArray
import android.view.MotionEvent
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import com.lu.magic.util.AppUtil


class PermissionUtil() {

    companion object {
        const val REQUEST_CODE = 200
        const val KEY_PERMISSION_LIST = "KEY_PERMISSION_LIST"
        const val KEY_REQUEST_ID = "KEY_REQUEST_ID"

        //        val sCallbacks: SparseArray<CallBack> = SparseArray()
        val sRequestTemp = SparseArray<Request>()

        /**
         * 待请求权限列表
         */
        fun permission(vararg permissions: String): Request {
            return Request(permissions)
        }


        /**
         * 待请求权限列表
         */
        fun permission(requestCode: Int, vararg permissions: String): Request {
            return Request(requestCode, permissions)
        }

        /**
         * 检查是否具备权限
         */
        fun hasPermission(vararg permissions: String): Boolean {
            return hasPermission(AppUtil.getContext(), arrayListOf(*permissions))
        }

        /**
         * 检查是否具备权限
         */
        fun hasPermission(context: Context, vararg permissions: String): Boolean {
            return hasPermission(context, arrayListOf(*permissions))
        }

        /**
         * 检查是否具备权限
         */
        fun hasPermission(context: Context, permissions: List<String>): Boolean {
            for (p in permissions) {
                val code = ActivityCompat.checkSelfPermission(context, p)
                if (code != PermissionChecker.PERMISSION_GRANTED) {
                    return false
                }
            }
            return true
        }

        internal fun allValueGranted(vararg result: Int): Boolean {
            for (value in result) {
                if (value != PermissionChecker.PERMISSION_GRANTED) {
                    return false
                }
            }
            return true
        }
    }

    @FunctionalInterface
    fun interface CallBack {
        fun onRequestPermissionsResult(
            requestCode: Int,
            result: Map<String, Int>,
            allGranted: Boolean
        )
    }

    /**
     * 权限请求封装类
     * example：Request(Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION).call()
     */
    class Request(val requestCode: Int, val permissions: Array<out String>) {
        constructor(permissions: Array<out String>) : this(REQUEST_CODE, permissions)

        var callback: CallBack = object : CallBack {
            override fun onRequestPermissionsResult(requestCode: Int, result: Map<String, Int>, allGranted: Boolean) {

            }
        }

        fun call(context: Context? = AppUtil.getContext()) {
            if (hasPermission(*permissions)) {
                callback.onRequestPermissionsResult(
                    requestCode,
                    permissions.indices.associate {
                        permissions[it] to PermissionChecker.PERMISSION_GRANTED
                    },
                    true
                )
                return
            }
            val intent = Intent(
                context,
                PermissionActivity::class.java
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putStringArrayListExtra(
                KEY_PERMISSION_LIST,
                permissions.toCollection(ArrayList<String>())
            )

            val requestKey = hashCode()
            intent.putExtra(KEY_REQUEST_ID, requestKey)
            sRequestTemp.put(requestKey, this)

            context?.startActivity(intent)
        }

        fun onResult(
            block: CallBack
        ): Request {
            callback = block
            return this
        }
    }
}

class PermissionActivity : Activity() {

    private lateinit var callBack: PermissionUtil.CallBack
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(android.R.style.Theme_Translucent_NoTitleBar_Fullscreen)
        super.onCreate(savedInstanceState)

        val permissions: ArrayList<String>? =
            intent.getStringArrayListExtra(PermissionUtil.KEY_PERMISSION_LIST)
        if (permissions == null || permissions.size == 0) {
            finish()
            return
        }

        val requestId = intent.getIntExtra(PermissionUtil.KEY_REQUEST_ID, 0)
        val request = PermissionUtil.sRequestTemp[requestId]

        callBack = request.callback
        PermissionUtil.sRequestTemp.remove(requestId)

        requestPermissions(permissions.toTypedArray(), PermissionUtil.REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val result = permissions.indices.associate {
            Pair(permissions[it], grantResults[it])
        }
        callBack.onRequestPermissionsResult(requestCode, result, PermissionUtil.allValueGranted(*grantResults))
        finish()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_UP) {
            finish()
        }
        return super.onTouchEvent(event)
    }
}