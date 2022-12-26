package com.lu.magic.frame.xp

import android.content.Context
import android.content.SharedPreferences
import com.lu.magic.frame.xp.annotation.FunctionValue
import com.lu.magic.frame.xp.annotation.GroupValue
import com.lu.magic.frame.xp.annotation.ModeValue
import com.lu.magic.frame.xp.annotation.PreferenceIdValue
import com.lu.magic.frame.xp.bean.ContractRequest
import com.lu.magic.frame.xp.bean.ContractResponse
import com.tencent.mmkv.MMKV
import java.io.Serializable

class PreferenceServerImpl(val context: Context) {


    fun call(request: ContractRequest): ContractResponse<*>? {
        var response: ContractResponse<*>? = null
        if (request.actions == null || request.actions.size == 0) {
            return null
        }
        when (request.mode) {
            ModeValue.READ -> response = readValue(request)
            ModeValue.WRITE -> response = writeValue(request)
            else -> {}
        }
        response?.responseId = request.requestId
        return response
    }


    fun readValue(request: ContractRequest): ContractResponse<*>? {
        //偏好设置读取时，只有一次操作
        val bundleAction = request.actions[0]
        val group = request.group
        val key = bundleAction.key
        var resultV = bundleAction.value
        when (group) {
            GroupValue.GET -> resultV = getValue(request)
            GroupValue.CONTAINS ->                 //TODO fix bug
                resultV = containsKey(request.preferenceId, request.table, key)
        }
        return ContractResponse(resultV, null)
    }

    private fun containsKey(@PreferenceIdValue preferenceId: String, table: String, key: String): Boolean {
        return if (PreferenceIdValue.MMKV == preferenceId) {
            mmkvTable(table).contains(key)
        } else prefsTable(table).contains(key)
    }


    private fun getValue(request: ContractRequest): Serializable? {
        val table = request.table
        val action = request.actions[0]
        val function = action.function
        val key = action.key
        val defValue = action.value
        val sp: SharedPreferences = if (PreferenceIdValue.MMKV == request.preferenceId) {
            mmkvTable(table)
        } else {
            prefsTable(table)
        }
        var resultV: Serializable? = null
        when (function) {
            FunctionValue.GET_STRING -> resultV = sp.getString(key, defValue as String)
            FunctionValue.GET_INT -> resultV = sp.getInt(key, (defValue as Int))
            FunctionValue.GET_LONG -> resultV = sp.getLong(key, (defValue as Long))
            FunctionValue.GET_FLOAT -> resultV = sp.getFloat(key, (defValue as Float))
            FunctionValue.GET_STRING_SET -> {
                var setResult = sp.getStringSet(key, defValue as Set<String?>)
                if (setResult !is Serializable) {
                    setResult = LinkedHashSet(setResult)
                }
                resultV = setResult as Serializable?
            }

            FunctionValue.GET_ALL -> {
                var mapResult = sp.all
                if (mapResult !is Serializable) {
                    mapResult = LinkedHashMap(mapResult)
                }
                resultV = mapResult as Serializable
            }

            FunctionValue.GET_BOOLEAN -> resultV = sp.getBoolean(key, (defValue as Boolean))
            else -> {}
        }
        return resultV
    }


    private fun writeValue(request: ContractRequest): ContractResponse<*>? {
        when (request.group) {
            GroupValue.COMMIT -> return commitValue(request)
            GroupValue.APPLY -> return applyValue(request)
            else -> {}
        }
        return ContractResponse<Any?>(null, null)
    }

    private fun commitValue(request: ContractRequest): ContractResponse<Boolean> {
        val editor = openEditor(request.preferenceId, request.table)
        putValue(editor, request.actions)
        val result = editor.commit()
        return ContractResponse(result, null)
    }

    private fun applyValue(request: ContractRequest): ContractResponse<Void?> {
        val editor = openEditor(request.preferenceId, request.table)
        putValue(editor, request.actions)
        editor.apply()
        return ContractResponse(null, null)
    }

    private fun putValue(editor: SharedPreferences.Editor, bundleActions: List<ContractRequest.Action<*>>) {
        for (action in bundleActions) {
            val function = action.function
            val key = action.key
            val value = action.value
            when (function) {
                FunctionValue.CLEAR -> editor.clear()
                FunctionValue.REMOVE -> editor.remove(key)
                else -> if (value is String) {
                    editor.putString(key, value)
                } else if (value is Int) {
                    editor.putInt(key, value)
                } else if (value is Boolean) {
                    editor.putBoolean(key, value)
                } else if (value is Float) {
                    editor.putFloat(key, value)
                } else if (value is Long) {
                    editor.putLong(key, value)
                } else if (value is Set<*>) {
                    val genericType = value.javaClass.genericInterfaces
                    editor.putStringSet(key, value as Set<String?>)
                }
            }
        }
    }

    private fun mmkvTable(table: String): MMKV {
        return MMKV.mmkvWithID(table)
    }


    private fun prefsTable(name: String): SharedPreferences {
        /**
         * 偏好设置实现类源码(SharedPreferencesImpl)已经做了内存优化，commit/apply，刷新内存缓存mMap,
         * 只要sp实例不变，就不需要再实现内存缓存
         */
        return context.getSharedPreferences(name, Context.MODE_PRIVATE)
    }


    private fun openEditor(@PreferenceIdValue preferenceId: String, table: String): SharedPreferences.Editor {
        return if (PreferenceIdValue.MMKV == preferenceId) {
            mmkvTable(table)
        } else prefsTable(table).edit()
    }
}