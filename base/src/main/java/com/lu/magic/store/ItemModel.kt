package com.lu.magic.store;


import com.lu.magic.IModuleFace
import com.lu.magic.IModuleFace.ModuleEmpty
import java.io.Serializable
import kotlin.reflect.KClass

open class ItemModel(
    var name: String,
    var page: PageModel,
    var moduleClassName: String = ModuleEmpty::class.java.name
) : Serializable {

    // 使用moduleClassName而不是class
    // 因为IModuleFace中的Magic类使用了只编译不打包的jar（xposed）
    // 无法被序列化，intent或其他序列化传输时会崩溃
    constructor(
        name: String,
        page: PageModel,
        moduleClass: KClass<out IModuleFace>
    ) : this(name, page, moduleClass.java.name)

    constructor(
        name: String,
        page: PageModel,
        moduleClass: Class<out IModuleFace>
    ) : this(name, page, moduleClass.name)

}

class PageModel(
    var title: String,
    var sheet: String,
    var land: Boolean
) : Serializable {
    constructor() : this("", "", false)
    constructor(title: String, sheet: String) : this(title, sheet, false)

}
