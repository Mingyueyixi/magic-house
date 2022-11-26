package com.lu.magic

import kotlin.reflect.KClass

object ModuleProviders {
    @JvmField
    var moduleFaces: MutableMap<String, IModuleFace> = mutableMapOf()


    fun put(vararg modules: IModuleFace) {
        for (module in modules) {
            moduleFaces[getModuleKey(module.javaClass)] = module
        }
    }

    private fun <T : IModuleFace> getModule(clazz: Class<T>, key: String): IModuleFace? {
        var key = getModuleKey(clazz)
        var instance = moduleFaces[key]
        if (instance == null) {
            try {
                instance = clazz.newInstance()
                moduleFaces[key] = instance
            } catch (e: Exception) {
            }
        }
        return instance
    }

    fun get(clazzName: String): IModuleFace? {
        try {
            val clazz = Class.forName(clazzName) as Class<out IModuleFace>
            return getModule(clazz, clazzName)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun <T : IModuleFace> get(clazz: Class<T>): IModuleFace? {
        return getModule(clazz, getModuleKey(clazz))
    }

    fun <T : IModuleFace> get(clazz: KClass<T>): IModuleFace? {
        return get(clazz.java)
    }


    private fun <T : IModuleFace> getModuleKey(clazz: Class<T>): String {
        return clazz.name
    }

}