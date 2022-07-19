package com.lu.code.magic.bean;

/**
 * @Author: Lu
 * Date: 2022/04/06
 * Description:
 */
public class BaseConfig<T> {
    private boolean enable;
    private T data;

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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
