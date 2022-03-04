package com.lu.code.magic.main.store;


import androidx.fragment.app.Fragment
import java.io.Serializable

open class ItemModel(
    var name: String,
    var page: PageModel
) : Serializable {

}

class PageModel(
    var title: String,
    var pageCls: Class<out Fragment>?,
) : Serializable {
    constructor() : this("", null)
}
