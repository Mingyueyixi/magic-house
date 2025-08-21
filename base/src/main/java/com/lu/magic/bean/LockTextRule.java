package com.lu.magic.bean;

import com.lu.magic.util.JSONX;
import com.lu.magic.util.JsonEncoder;

import org.json.JSONObject;

public class LockTextRule extends JsonBean {
    public boolean lockAll;
    public boolean deepCheck;
    public String keyWord;
    public String kwMode;
    public boolean matchDotLine;
    public String title;
    public String mess;
    public String password;

    public static LockTextRule fromJson(JSONObject data) {
        LockTextRule result = new LockTextRule();
        result.lockAll = JSONX.optBoolean(data, "lockAll");
        result.deepCheck = JSONX.optBoolean(data, "deepCheck");
        result.keyWord = JSONX.optString(data, "keyWord");
        result.kwMode = JSONX.optString(data, "kwMode");
        result.matchDotLine = JSONX.optBoolean(data, "matchDotLine");
        result.title = JSONX.optString(data, "title");
        result.mess = JSONX.optString(data, "mess");
        result.password = JSONX.optString(data, "password");
        return result;
    }

    @Override
    public JSONObject toJson() {
        JSONObject result = new JSONObject();
        JSONX.putOpt(result, "lockAll", lockAll);
        JSONX.putOpt(result, "deepCheck", deepCheck);
        JSONX.putOpt(result, "keyWord", keyWord);
        JSONX.putOpt(result, "kwMode", kwMode);
        JSONX.putOpt(result, "matchDotLine", matchDotLine);
        JSONX.putOpt(result, "title", title);
        JSONX.putOpt(result, "mess", mess);
        JSONX.putOpt(result, "password", password);
        return result;
    }

}
