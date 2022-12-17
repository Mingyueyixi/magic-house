package com.lu.magic.frame.xp

import android.content.Context
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Parcelable
import com.lu.magic.frame.xp.annotation.FunctionValue
import com.lu.magic.frame.xp.annotation.GroupValue
import com.lu.magic.frame.xp.annotation.ModeValue
import com.lu.magic.frame.xp.annotation.PreferenceIdValue
import com.lu.magic.frame.xp.bean.ContractRequest
import com.lu.magic.frame.xp.broadcast.BRConfig
import com.lu.magic.frame.xp.broadcast.ClientSession
import com.lu.magic.frame.xp.broadcast.PreferenceClientReceiver
import com.lu.magic.frame.xp.broadcast.PreferenceServerReceiver
import com.lu.magic.frame.xp.util.log.XPLogUtil
import java.io.Serializable
import java.util.concurrent.atomic.AtomicInteger

class BRPreference(
    val context: Context,
    private val tableName: String?,
    @PreferenceIdValue val preferenceId: String,
    val config: BRConfig,
) : SharedPreferences {

    override fun getAll(): Map<String, *> {
        return getValue(FunctionValue.GET_ALL, null, HashMap<Any, Any>(), Map::class.java) as Map<String, *>
    }

    override fun getString(key: String, defValue: String?): String? {
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
            preferenceId,
            ModeValue.READ,
            tableName,
            GroupValue.CONTAINS,
            listOf<ContractRequest.Action<*>>(action)
        )
        val response = ClientSession(config).requestWithBlock(context, request, Boolean::class.java)
        return response.data ?: false
    }

    override fun edit(): SharedPreferences.Editor {
        return XEditor()
    }

    override fun registerOnSharedPreferenceChangeListener(listener: OnSharedPreferenceChangeListener) {

    }

    override fun unregisterOnSharedPreferenceChangeListener(listener: OnSharedPreferenceChangeListener) {

    }

    private fun <T> getValue(function: String, key: String?, defValue: T?, rClass: Class<T>): T? {
        val action = ContractRequest.Action(function, key, defValue)
        val request = ContractRequest(
            preferenceId,
            ModeValue.READ,
            tableName,
            GroupValue.GET,
            listOf<ContractRequest.Action<*>>(action)
        )
        val response = ClientSession(config).requestWithBlock(context, request, rClass)
        val data = response.data
        return data ?: defValue
    }

    inner class XEditor : SharedPreferences.Editor {
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
                preferenceId, ModeValue.WRITE, tableName, GroupValue.COMMIT, ArrayList(mActionMap.values)
            )
            val response = ClientSession(config).requestWithBlock(context, request, Boolean::class.java)
            return response.data != null && response.data!!
        }

        override fun apply() {
            val request = ContractRequest(
                preferenceId, ModeValue.WRITE, tableName, GroupValue.COMMIT, ArrayList(mActionMap.values)
            )
            ClientSession(config).request(context, request, Any::class.java, null)
        }
    }

    companion object {
        private val clientReceiverStore = mutableMapOf<String, PreferenceClientReceiver>()
        private val serverReceiverStore = mutableMapOf<String, PreferenceServerReceiver>()

        fun registerAsClient(context: Context, config: BRConfig): PreferenceClientReceiver {
            val receiver = PreferenceClientReceiver()
            val filter = IntentFilter()
            filter.addAction(config.clientAction)
            context.registerReceiver(receiver, filter)

            //解绑旧的receiver
            val storeReceiver = clientReceiverStore[config.clientAction]
            if (storeReceiver != null) {
                context.unregisterReceiver(storeReceiver)
                XPLogUtil.w("unRegister a old clientReceiver")
            }
            clientReceiverStore[config.clientAction] = receiver
            return receiver
        }

        fun registerAsServer(context: Context, config: BRConfig): PreferenceServerReceiver {
            val receiver = PreferenceServerReceiver(config)
            val filter = IntentFilter()
            filter.addAction(config.serverAction)
            context.registerReceiver(receiver, filter)
            //解绑旧的receiver
            val storeReceiver = serverReceiverStore[config.serverAction]
            if (storeReceiver != null) {
                context.unregisterReceiver(storeReceiver)
                XPLogUtil.w("unRegister a old serverReceiver")
            }
            serverReceiverStore[config.serverAction] = receiver
            return receiver
        }

        fun unRegisterClient(context: Context, action: String) {
            val receiver = clientReceiverStore.remove(action)
            context.unregisterReceiver(receiver)
        }

        fun unRegisterServer(context: Context, action: String) {
            val receiver = serverReceiverStore.remove(action)
            context.unregisterReceiver(receiver)
        }

        fun unRegisterAllClient(context: Context) {
            val it = clientReceiverStore.iterator()
            while (it.hasNext()) {
                val ele = it.next()
                it.remove()
                context.unregisterReceiver(ele.value)
            }
        }

        fun unRegisterAllServer(context: Context) {
            val it = serverReceiverStore.iterator()
            while (it.hasNext()) {
                val ele = it.next()
                it.remove()
                context.unregisterReceiver(ele.value)
            }
        }

    }
}