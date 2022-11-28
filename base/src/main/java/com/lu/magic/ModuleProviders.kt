package com.lu.magic

import com.lu.magic.util.config.SheetName

object ModuleProviders {
    @JvmField
    var moduleFaces: MutableMap<String, IModuleFace> = mutableMapOf()

    fun put(@SheetName key: String, module: IModuleFace) {
        moduleFaces[key] = module
    }

    fun get(key: String): IModuleFace? {
        return moduleFaces[key]
    }

}