package com.lu.magic.store;


import com.lu.magic.config.ModuleId
import java.io.Serializable

open class ItemModel(
    var name: String,
    var module: ModuleModel
) : Serializable

class ModuleModel(
    var title: String = "",
    @ModuleId var moduleId: String = ModuleId.EMPTY,
    var land: Boolean = false
) : Serializable
