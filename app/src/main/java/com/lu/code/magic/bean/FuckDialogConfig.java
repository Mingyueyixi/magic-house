package com.lu.code.magic.bean;

public class FuckDialogConfig extends BaseConfig {
    private String keyword;
    private String mode;
    private NormalModeDTO normalMode;
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
