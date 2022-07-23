package com.lu.magic.bean;

public class ViewCatchConfig extends BaseConfig<ViewCatchConfig.Feature> {

    public static class Feature {
        public boolean activityResume;
        public boolean fragmentResume;
        public boolean viewClick;
    }
}
