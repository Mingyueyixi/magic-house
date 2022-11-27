package com.lu.magic.store;


import com.lu.magic.IModuleFace
import com.lu.magic.IModuleFace.ModuleEmpty
import java.io.Serializable
import kotlin.reflect.KClass

open class ItemModel(
    var name: String,
    var page: PageModel,
    var moduleKey: String = ModuleEmpty.MODULE_KEY
) : Serializable

class PageModel(
    var title: String,
    var sheet: String,
    var land: Boolean
) : Serializable {
    constructor() : this("", "", false)
    constructor(title: String, sheet: String) : this(title, sheet, false)

}
