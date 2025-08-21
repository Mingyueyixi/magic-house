package com.lu.magic.util;

import org.json.JSONObject;

/**
 * @author Lu
 * @date 2025/8/11
 * @description
 */
public interface JsonDecoder<T> {

    T fromJson(JSONObject data);
}
