package com.lu.code.magic.bean;

/**
 * @Author: Lu
 * Date: 2022/04/06
 * Description:
 */
public class BaseConfig {
    private boolean enable;

    public BaseConfig() {
    }

    public BaseConfig(boolean enable) {
        this.enable = enable;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
