package com.lu.magic.config

import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.lu.magic.util.AppUtil
import com.lu.magic.util.GsonUtil
import com.lu.magic.util.permission.PermissionUtil
import java.io.File


object ConfigPreference {

    fun getReadable(): ReadOnlySharedPreference {
        val data = BackupAction(getWritable()).readAll()
        return ReadOnlySharedPreference(data)
    }

    fun getWritable(): WriteSharedPreference {
        return WriteSharedPreference("com.lu.magic", "config")
    }

}

class WriteSharedPreference(val parentDirName: String, val fileName: String) : SharedPreferences {
    val sp: SharedPreferences = AppUtil.getContext().getSharedPreferences(fileName, Context.MODE_PRIVATE)
    val backupAction = BackupAction(this)

    override fun getAll(): MutableMap<String, *> {
        return sp.all
    }

    override fun getString(key: String?, defValue: String?): String? {
        return sp.getString(key, defValue)
    }

    override fun getStringSet(key: String?, defValues: MutableSet<String>?): MutableSet<String>? {
        return sp.getStringSet(key, defValues)
    }

    override fun getInt(key: String?, defValue: Int): Int {
        return sp.getInt(key, defValue)
    }

    override fun getLong(key: String?, defValue: Long): Long {
        return getLong(key, defValue)
    }

    override fun getFloat(key: String?, defValue: Float): Float {
        return getFloat(key, defValue)
    }

    override fun getBoolean(key: String?, defValue: Boolean): Boolean {
        return getBoolean(key, defValue)
    }

    override fun contains(key: String?): Boolean {
        return contains(key)
    }

    override fun edit(): SharedPreferences.Editor {
        return Editor(this, this.sp.edit())
    }

    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        sp.registerOnSharedPreferenceChangeListener(listener)
    }

    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        sp.unregisterOnSharedPreferenceChangeListener(listener)
    }

    class Editor(private val wsp: WriteSharedPreference, private val editor: SharedPreferences.Editor) :
        SharedPreferences.Editor {
        override fun putString(key: String?, value: String?): SharedPreferences.Editor {
            editor.putString(key, value)
            return this
        }

        override fun putStringSet(key: String?, values: MutableSet<String>?): SharedPreferences.Editor {
            editor.putStringSet(key, values)
            return this

        }

        override fun putInt(key: String?, value: Int): SharedPreferences.Editor {
            editor.putInt(key, value)
            return this
        }

        override fun putLong(key: String?, value: Long): SharedPreferences.Editor {
            editor.putLong(key, value)
            return this
        }

        override fun putFloat(key: String?, value: Float): SharedPreferences.Editor {
            editor.putFloat(key, value)
            return this
        }

        override fun putBoolean(key: String?, value: Boolean): SharedPreferences.Editor {
            editor.putBoolean(key, value)
            return this
        }

        override fun remove(key: String?): SharedPreferences.Editor {
            editor.remove(key)
            return this
        }

        override fun clear(): SharedPreferences.Editor {
            editor.clear()
            return this
        }

        override fun commit(): Boolean {
            wsp.backupAction.backup()
            return editor.commit()
        }

        override fun apply() {
            wsp.backupAction.backup()
            editor.apply()
        }
    }
}

class ReadOnlySharedPreference(private val data: JsonObject) : SharedPreferences {

    override fun getAll(): MutableMap<String, *> {
        return data.asMap()
    }

    private inline fun <T> getInline(key: String?, defValue: T?, convertFunc: (v: JsonElement) -> T?): T? {
        val v = data[key]
        return if (v == null) defValue else {
            runCatching {
                convertFunc(v)
            }.getOrDefault(defValue)
        }
    }

    override fun getString(key: String?, defValue: String?): String? {
        return getInline(key, defValue) { it.asString }
    }

    override fun getStringSet(key: String?, defValues: MutableSet<String>?): MutableSet<String>? {
        return getInline(key, defValues) {
            GsonUtil.fromJson(it, GsonUtil.getType(LinkedHashSet::class.java, String::class.java))
        }
    }

    override fun getInt(key: String?, defValue: Int): Int {
        return getInline(key, defValue) { it.asInt } ?: defValue
    }

    override fun getLong(key: String?, defValue: Long): Long {
        return getInline(key, defValue) { it.asLong } ?: defValue
    }

    override fun getFloat(key: String?, defValue: Float): Float {
        return getInline(key, defValue) { it.asFloat } ?: defValue
    }

    override fun getBoolean(key: String?, defValue: Boolean): Boolean {
        return getInline(key, defValue) { it.asBoolean } ?: defValue
    }

    override fun contains(key: String?): Boolean {
        return data.has(key)
    }

    override fun edit(): SharedPreferences.Editor {
        throw NotImplementedError("Not yet implemented")
    }

    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        throw NotImplementedError("Not yet implemented")
    }

    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        throw NotImplementedError("Not yet implemented")
    }
}

class BackupAction(val wsp: WriteSharedPreference) {
    val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val configFile = File("${downloadDir.absolutePath}/${wsp.parentDirName}", wsp.fileName)

    //mediaStore数据库的相对路径最后可能带有/，比较是否相等时需要注意
    val relativePath = "${Environment.DIRECTORY_DOWNLOADS}/${wsp.parentDirName}"

    fun backup() {
        val dataMap = wsp.all
        val json = GsonUtil.getPrettyGson().toJson(dataMap)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            backupMediaWay(configFile, json)
        } else {
            backupFileWay(configFile, json)
        }
    }

    fun readAll(): JsonObject {
        return runCatching {
            val jsonText = configFile.readText()
            GsonUtil.fromJson(
                jsonText,
                JsonObject::class.java
            )
        }.getOrDefault(JsonObject())
    }

    private fun backupFileWay(configFile: File, json: String) {
        configFile.writeText(json)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun backupMediaWay(configFile: File, json: String) {
        val resolver = AppUtil.getContext().contentResolver
        //不管存在不存在，先删除
        resolver.delete(
            MediaStore.Downloads.EXTERNAL_CONTENT_URI,
            "${MediaStore.Images.Media.DISPLAY_NAME} =? AND (${MediaStore.Images.Media.RELATIVE_PATH} =? OR ${MediaStore.Images.Media.RELATIVE_PATH} =?)",
            arrayOf(configFile.name, relativePath, "$relativePath/")
        )
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, configFile.name)
            put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath)
        }

        resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)?.let {
            resolver.openOutputStream(it).use { out ->
                out?.write(json.toByteArray())
            }
        }

    }

}