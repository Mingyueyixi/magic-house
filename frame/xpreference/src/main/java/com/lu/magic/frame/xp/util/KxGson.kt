package com.lu.magic.frame.xp.util

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

internal class KxGson {
    companion object {
        @JvmStatic
        val GSON: Gson by lazy<Gson> {
            GsonBuilder().create()
        }
        val GSON_PRETTY: Gson by lazy<Gson> {
            GsonBuilder().setPrettyPrinting().create()
        }


        fun getListType(type: Type?): Type? {
            return TypeToken.getParameterized(MutableList::class.java, type).type
        }

        fun getSetType(type: Type?): Type? {
            return TypeToken.getParameterized(MutableSet::class.java, type).type
        }

        fun getMapType(keyType: Type?, valueType: Type?): Type? {
            return TypeToken.getParameterized(MutableMap::class.java, keyType, valueType).type
        }


        fun getArrayType(type: Type?): Type? {
            return TypeToken.getArray(type).type
        }

        fun getType(rawType: Type?, vararg typeArguments: Type?): Type? {
            return TypeToken.getParameterized(rawType, *typeArguments).type
        }
    }

}