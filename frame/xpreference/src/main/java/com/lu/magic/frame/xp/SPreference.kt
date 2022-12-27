package com.lu.magic.frame.xp

import android.content.Context
import android.content.SharedPreferences
import android.os.Parcelable
import com.lu.magic.frame.xp.annotation.FunctionValue
import com.lu.magic.frame.xp.annotation.GroupValue
import com.lu.magic.frame.xp.annotation.ModeValue
import com.lu.magic.frame.xp.annotation.PreferenceIdValue
import com.lu.magic.frame.xp.bean.ContractRequest
import com.lu.magic.frame.xp.socket.PreferencesSocketServer
import com.lu.magic.frame.xp.socket.RequestTransfer
import java.io.Serializable
import java.util.concurrent.atomic.AtomicInteger

class SPreference(val context: Context, val impl: SharedPreferences) : SharedPreferences {

    companion object {
        @JvmStatic
        fun initServer(context: Context, port: Int) {
            PreferencesSocketServer(context.applicationContext, port).start()
        }

        @JvmStatic
        fun getRemoteImpl(
            context: Context, tableName: String, @PreferenceIdValue preferenceId: String, port: Int
        ): SPreference {
            val impl = RemotePreferences(context.applicationContext, tableName, preferenceId, port)
            return SPreference(context.applicationContext, impl)
        }

        @JvmStatic
        fun getLocalImpl(context: Context, tableName: String): SPreference {
            val impl = context.getSharedPreferences(tableName, Context.MODE_PRIVATE)
            return SPreference(context.applicationContext, impl)
        }

    }

    override fun getAll(): MutableMap<String, *> {
        return impl.all
    }

    override fun getString(key: String?, defValue: String?): String? {
        return impl.getString(key, defValue)
    }

    override fun getStringSet(key: String?, defValues: MutableSet<String>?): MutableSet<String>? {
        return impl.getStringSet(key, defValues)
    }

    override fun getInt(key: String?, defValue: Int): Int {
        return impl.getInt(key, defValue)
    }

    override fun getLong(key: String?, defValue: Long): Long {
        return impl.getLong(key, defValue)
    }

    override fun getFloat(key: String?, defValue: Float): Float {
        return impl.getFloat(key, defValue)
    }

    override fun getBoolean(key: String?, defValue: Boolean): Boolean {
        return impl.getBoolean(key, defValue)
    }

    override fun contains(key: String?): Boolean {
        return impl.contains(key)
    }

    override fun edit(): SharedPreferences.Editor {
        return impl.edit()
    }

    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        impl.registerOnSharedPreferenceChangeListener(listener)
    }

    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        impl.unregisterOnSharedPreferenceChangeListener(listener)
    }


    class RemotePreferences(
        val context: Context, val tableName: String, @PreferenceIdValue val preferenceId: String, val port: Int
    ) : SharedPreferences {

        override fun getAll(): Map<String, *> {
            return getValue(FunctionValue.GET_ALL, null, HashMap<Any, Any>(), Map::class.java) as Map<String, *>
        }

        override fun getString(key: String?, defValue: String?): String? {
            return getValue(FunctionValue.GET_STRING, key, defValue, String::class.java)
        }


        override fun getStringSet(key: String, defValues: Set<String>?): Set<String>? {
            return getValue(FunctionValue.GET_STRING_SET, key, defValues, Set::class.java) as Set<String>?
        }

        override fun getInt(key: String, defValue: Int): Int {
            return getValue(FunctionValue.GET_INT, key, defValue, Int::class.java) ?: defValue
        }

        override fun getLong(key: String, defValue: Long): Long {
            return getValue(FunctionValue.GET_LONG, key, defValue, Long::class.java) ?: defValue
        }

        override fun getFloat(key: String, defValue: Float): Float {
            return getValue(FunctionValue.GET_FLOAT, key, defValue, Float::class.java) ?: defValue
        }

        override fun getBoolean(key: String, defValue: Boolean): Boolean {
            return getValue(FunctionValue.GET_BOOLEAN, key, defValue, Boolean::class.java) ?: defValue
        }

        override fun contains(key: String): Boolean {
            val action = ContractRequest.Action<Any?>(FunctionValue.CONTAINS, key, null)
            val request = ContractRequest(
                preferenceId, ModeValue.READ, tableName, GroupValue.CONTAINS, listOf<ContractRequest.Action<*>>(action)
            )
            val response = RequestTransfer(port).requestWithBlock(context, request, Boolean::class.java)
            return response.data ?: false
        }

        override fun edit(): SharedPreferences.Editor {
            return RemoteEditor(this)
        }

        override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
            throw NotImplementedError("Not yet implemented")
        }

        override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
            throw NotImplementedError("Not yet implemented")
        }


        private fun <T> getValue(function: String, key: String?, defValue: T?, rClass: Class<T>): T? {
            val action = ContractRequest.Action(function, key, defValue)
            val request = ContractRequest(
                preferenceId, ModeValue.READ, tableName, GroupValue.GET, listOf<ContractRequest.Action<*>>(action)
            )
            val response = RequestTransfer(port).requestWithBlock(context, request, rClass)
            val data = response.data
            return data ?: defValue
        }

    }

    class RemoteEditor(val sp: RemotePreferences) : SharedPreferences.Editor {
        private val mActionMap = LinkedHashMap<String, ContractRequest.Action<*>>()
        private val atomicClearMask = AtomicInteger()
        override fun putString(key: String, value: String?): SharedPreferences.Editor {
            mActionMap[key] = ContractRequest.Action(FunctionValue.PUT_STRING, key, value)
            return this
        }

        override fun putStringSet(key: String, values: Set<String>?): SharedPreferences.Editor {
            val action = ContractRequest.Action(FunctionValue.PUT_STRING_SET, key, values)
            if (!(values is Serializable || values is Parcelable)) {
                action.value = LinkedHashSet(values)
            }
            mActionMap[key] = action
            return this
        }

        override fun putInt(key: String, value: Int): SharedPreferences.Editor {
            mActionMap[key] = ContractRequest.Action(FunctionValue.PUT_INT, key, value)
            return this
        }

        override fun putLong(key: String, value: Long): SharedPreferences.Editor {
            mActionMap[key] = ContractRequest.Action(FunctionValue.PUT_LONG, key, value)
            return this
        }

        override fun putFloat(key: String, value: Float): SharedPreferences.Editor {
            mActionMap[key] = ContractRequest.Action(FunctionValue.PUT_FLOAT, key, value)
            return this
        }

        override fun putBoolean(key: String, value: Boolean): SharedPreferences.Editor {
            mActionMap[key] = ContractRequest.Action(FunctionValue.PUT_BOOLEAN, key, value)
            return this
        }

        override fun remove(key: String): SharedPreferences.Editor {
            mActionMap[key] = ContractRequest.Action<Any?>(FunctionValue.REMOVE, key, null)
            return this
        }

        override fun clear(): SharedPreferences.Editor {
            //clear没有key，所以构建一个
            val key = "clear_" + hashCode() + "_" + atomicClearMask.incrementAndGet()
            mActionMap[key] = ContractRequest.Action<Any?>(FunctionValue.CLEAR, null, null)
            return this
        }

        override fun commit(): Boolean {
            val request = ContractRequest(
                sp.preferenceId, ModeValue.WRITE, sp.tableName, GroupValue.COMMIT, ArrayList(mActionMap.values)
            )
            val response = RequestTransfer(sp.port).requestWithBlock(sp.context, request, Boolean::class.java)
            return response.data != null && response.data!!
        }

        override fun apply() {
            val request = ContractRequest(
                sp.preferenceId, ModeValue.WRITE, sp.tableName, GroupValue.COMMIT, ArrayList(mActionMap.values)
            )
            RequestTransfer(sp.port).request(sp.context, request, Any::class.java, null)
        }
    }

}