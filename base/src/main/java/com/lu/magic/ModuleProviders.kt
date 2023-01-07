package com.lu.magic

import com.lu.magic.config.ModuleId

object ModuleProviders {
    @JvmField
    var moduleFaces: MutableMap<String, IModuleFace> = mutableMapOf()

    fun put(@ModuleId key: String, module: IModuleFace) {
        moduleFaces[key] = module
    }

    fun get(key: String): IModuleFace? {
        return moduleFaces[key]
    }

}