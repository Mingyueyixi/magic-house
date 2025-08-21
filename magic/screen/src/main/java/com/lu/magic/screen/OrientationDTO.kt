package com.lu.magic.screen

import com.lu.magic.util.JSONX
import com.lu.magic.util.JsonEncoder
import org.json.JSONArray
import org.json.JSONObject

/**
 * Activity屏幕方向模板
 */
class OrientationDTO(var actList: List<ActItem>) : JsonEncoder {
    override fun toJson(): JSONObject {
        val json = JSONObject()
        val actListArray = JSONArray()
        for (item in actList) {
            actListArray.put(item.toJson())
        }
        json.put("actList", actListArray)
        return json
    }

    companion object {
        @JvmStatic
        fun fromJson(json: JSONObject?): OrientationDTO {
            val actList = mutableListOf<ActItem>()
            val actArray = JSONX.optJSONArray(json, "actList")
            if (actArray != null) {
                for (i in 0 until actArray.length()) {
                    val item = ActItem.fromJson(actArray.getJSONObject(i))
                    actList.add(item)
                }
            }
            return OrientationDTO(actList)
        }
    }

    class ActItem(var actClass: String, var orientation: Int, var enable: Boolean) : JsonEncoder {

        override fun toJson(): JSONObject {
            val json = JSONObject()
            json.put("actClass", actClass)
            json.put("orientation", orientation)
            json.put("enable", enable)
            return json
        }

        companion object {
            @JvmStatic
            fun fromJson(json: JSONObject?): ActItem {
                return ActItem(
                    JSONX.optString(json, "actClass"),
                    JSONX.optInt(json, "orientation"),
                    JSONX.optBoolean(json, "enable")
                )
            }
        }
    }

}