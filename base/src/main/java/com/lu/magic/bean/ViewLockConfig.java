package com.lu.magic.bean;

public class ViewLockConfig extends BaseConfig<ViewLockConfig.LockTextRule> {

    public static class LockTextRule {
        public boolean lockAll;
        public boolean deepCheck;
        public String keyWord;
        public String kwMode;
        public boolean matchDotLine;
        public String title;
        public String mess;
        public String password;
    }

}
