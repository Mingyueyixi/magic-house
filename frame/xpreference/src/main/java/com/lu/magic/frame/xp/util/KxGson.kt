package com.lu.magic.frame.xp.util

import com.google.gson.Gson
import com.google.gson.GsonBuilder

internal class KxGson {
    companion object {
        @JvmStatic
        val GSON: Gson by lazy<Gson> {
            GsonBuilder().setPrettyPrinting().create()
        }

    }

}