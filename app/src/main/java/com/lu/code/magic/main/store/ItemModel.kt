package com.lu.code.magic.main.store;


import java.io.Serializable

open class ItemModel(
    var name: String,
    var page: PageModel
) : Serializable {

}

class PageModel(
    var title: String,
    var sheet: String,
) : Serializable {
    constructor() : this("", "")
}
