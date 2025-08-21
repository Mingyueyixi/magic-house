package com.lu.magic.bean;

import androidx.annotation.NonNull;

import com.lu.magic.util.JsonEncoder;

import java.io.Serializable;

/**
 * @author Lu
 * @date 2025/8/21
 * @description
 */
public abstract class JsonBean implements JsonEncoder, Serializable {
    @NonNull
    @Override
    public String toString() {
        return toJson().toString();
    }
}
