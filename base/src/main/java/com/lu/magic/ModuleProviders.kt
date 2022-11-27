package com.lu.magic

import kotlin.reflect.KClass

object ModuleProviders {
    @JvmField
    var moduleFaces: MutableMap<String, IModuleFace> = mutableMapOf()

    fun put(key: String, module: IModuleFace) {
        moduleFaces[key] = module
    }

    fun get(key: String): IModuleFace? {
        return moduleFaces[key]
    }

}