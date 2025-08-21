package com.lu.magic.bean;

import androidx.annotation.NonNull;

import com.lu.magic.util.JSONX;
import com.lu.magic.util.JsonDecoder;
import com.lu.magic.util.JsonEncoder;

import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

/**
 * @Author: Lu
 * Date: 2022/04/06
 * Description:
 */
public class Config<T> extends JsonBean {
    protected boolean enable;
    @Nullable
    protected T data;

    public Config() {
    }

    public Config(boolean enable) {
        this.enable = enable;
    }

    public Config(boolean enable, T data) {
        this.enable = enable;
        this.data = data;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        JSONX.putOpt(jsonObject, "enable", enable);
        if (data instanceof JsonEncoder) {
            JSONX.putOpt(jsonObject, "data", ((JsonEncoder) data).toJson());
        } else {
            JSONX.putOpt(jsonObject, "data", data);
        }
        return jsonObject;
    }

    @NonNull
    @Override
    public String toString() {
        return toJson().toString();
    }

    public static Config<JSONObject> fromJson(JSONObject jsonObject) {
        return fromJson(jsonObject, data -> data);
    }

    public static <T> Config<T> fromJson(JSONObject jsonObject, JsonDecoder<T> factory) {
        Config<T> result = new Config<>();
        result.enable = JSONX.optBoolean(jsonObject, "enable");
        JSONObject dataObj = JSONX.optJSONObject(jsonObject, "data", new JSONObject());
        result.data = factory.fromJson(dataObj);
        return result;
    }

}
