package com.lu.code.magic.bean;

import java.io.Serializable;

public class Query<T> implements Serializable {
    private String key;
    private T value;
    private T defValue;
    private String function;

    public Query() {
    }

    public Query(String key, T value, T defValue, String action) {
        this.key = key;
        this.value = value;
        this.defValue = defValue;
        this.function = action;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public T getDefValue() {
        return defValue;
    }

    public void setDefValue(T defValue) {
        this.defValue = defValue;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }
}
