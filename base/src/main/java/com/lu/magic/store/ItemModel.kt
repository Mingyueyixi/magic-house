package com.lu.magic.store;


import java.io.Serializable

open class ItemModel(
    var name: String,
    var page: PageModel
) : Serializable {

}

class PageModel(
    var title: String,
    var sheet: String,
    var land: Boolean
) : Serializable {
    constructor() : this("", "", false)
    constructor(title: String, sheet: String) : this(title, sheet, false)

}
