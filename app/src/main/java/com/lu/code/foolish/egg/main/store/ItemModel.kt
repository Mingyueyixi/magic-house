package com.lu.code.foolish.egg.main.store;


import androidx.fragment.app.Fragment
import java.io.Serializable

open class ItemModel(var name: String, var pageCls: Class<out Fragment>?): Serializable
