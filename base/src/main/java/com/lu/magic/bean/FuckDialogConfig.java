package com.lu.magic.bean;

public class FuckDialogConfig extends BaseConfig {
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

    public FuckDialogConfig() {
        this(false, "", new NormalModeDTO(), new RegexModeDTO());
    }

    public FuckDialogConfig(boolean enable, String keyword, NormalModeDTO normalMode, RegexModeDTO regexMode) {
        super(enable);
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

    public static class NormalModeDTO {

    }

    public static class RegexModeDTO {
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
    }
}
