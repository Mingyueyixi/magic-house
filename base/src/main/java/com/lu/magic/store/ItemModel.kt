package com.lu.magic.store;


import com.lu.magic.util.config.SheetName
import java.io.Serializable

open class ItemModel(
    var name: String,
    var module: ModuleModel
) : Serializable

class ModuleModel(
    var title: String = "",
    @SheetName var sheet: String = SheetName.EMPTY,
    var land: Boolean = false
) : Serializable
