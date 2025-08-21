package com.lu.magic.bean;

import com.lu.magic.util.JSONX;
import com.lu.magic.util.JsonEncoder;
import org.json.JSONObject;

public class FuckDialogData extends JsonBean {
    /**
     * 搜索关键字
     */
    private String keyword;
    /**
     * 匹配模式
     */
    private String mode;
    /**
     * 强制隐藏
     */
    private boolean strongHide;
    /**
     * 弹出提示
     */
    private boolean promptTip;
    /**
     * 普通模式配置
     */
    private NormalModeDTO normalMode;
    /**
     * 正则模式配置
     */
    private RegexModeDTO regexMode;

    public FuckDialogData() {
        this(false, "", new NormalModeDTO(), new RegexModeDTO());
    }

    public FuckDialogData(boolean enable, String keyword, NormalModeDTO normalMode, RegexModeDTO regexMode) {
        this.mode = "normal";
        this.keyword = keyword;
        this.normalMode = normalMode;
        this.regexMode = regexMode;
    }

    public boolean isStrongHide() {
        return strongHide;
    }

    public boolean isPromptTip() {
        return promptTip;
    }

    public void setPromptTip(boolean promptTip) {
        this.promptTip = promptTip;
    }

    public void setStrongHide(boolean strongHide) {
        this.strongHide = strongHide;
    }

    public String getKeyword() {
        return keyword;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public NormalModeDTO getNormalMode() {
        return normalMode;
    }

    public void setNormalMode(NormalModeDTO normalMode) {
        this.normalMode = normalMode;
    }

    public RegexModeDTO getRegexMode() {
        return regexMode;
    }

    public void setRegexMode(RegexModeDTO regexMode) {
        this.regexMode = regexMode;
    }

    public static FuckDialogData fromJson(JSONObject jsonObject) {
        boolean enable = JSONX.optBoolean(jsonObject, "enable");
        String keyword = JSONX.optString(jsonObject, "keyword");
        String mode = JSONX.optString(jsonObject, "mode");
        boolean strongHide = JSONX.optBoolean(jsonObject, "strongHide");
        boolean promptTip = JSONX.optBoolean(jsonObject, "promptTip");
        NormalModeDTO normalMode = NormalModeDTO.fromJson(JSONX.optJSONObject(jsonObject, "normalMode", new JSONObject()));
        RegexModeDTO regexMode = RegexModeDTO.fromJson(JSONX.optJSONObject(jsonObject, "regexMode", new JSONObject()));
        FuckDialogData result = new FuckDialogData(enable, keyword, normalMode, regexMode);
        result.setMode(mode);
        result.setStrongHide(strongHide);
        result.setPromptTip(promptTip);
        return result;
    }

    @Override
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        JSONX.putOpt(jsonObject, "keyword", keyword);
        JSONX.putOpt(jsonObject, "mode", mode);
        JSONX.putOpt(jsonObject, "strongHide", strongHide);
        JSONX.putOpt(jsonObject, "promptTip", promptTip);
        JSONX.putOpt(jsonObject, "normalMode", normalMode.toJson());
        JSONX.putOpt(jsonObject, "regexMode", regexMode.toJson());
        return jsonObject;
    }

    public static class NormalModeDTO extends JsonBean {
        @Override
        public JSONObject toJson() {
            return new JSONObject();
        }

        public static NormalModeDTO fromJson(JSONObject normalMode) {
            return new NormalModeDTO();
        }
    }

    public static class RegexModeDTO extends JsonBean {
        private boolean ignoreCase;
        private boolean dotLine;


        public boolean isIgnoreCase() {
            return ignoreCase;
        }

        public void setIgnoreCase(boolean ignoreCase) {
            this.ignoreCase = ignoreCase;
        }

        public boolean isDotLine() {
            return dotLine;
        }

        public void setDotLine(boolean dotLine) {
            this.dotLine = dotLine;
        }

        @Override
        public JSONObject toJson() {
            JSONObject result = new JSONObject();
            JSONX.putOpt(result, "ignoreCase", ignoreCase);
            JSONX.putOpt(result, "dotLine", dotLine);
            return result;
        }

        public static RegexModeDTO fromJson(JSONObject regexMode) {
            RegexModeDTO result = new RegexModeDTO();
            result.ignoreCase = JSONX.optBoolean(regexMode, "ignoreCase");
            result.dotLine = JSONX.optBoolean(regexMode, "dotLine");
            return result;
        }
    }


}
