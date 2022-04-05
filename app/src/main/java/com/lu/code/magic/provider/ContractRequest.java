package com.lu.code.magic.provider;

import android.os.Bundle;

import androidx.core.app.BundleCompat;

import com.lu.code.magic.provider.annotation.FunctionValue;
import com.lu.code.magic.provider.annotation.GroupValue;

import java.util.Collection;
import java.util.List;

/**
 * ContentProvider约定的数据结构
 */
public class ContractRequest {
    protected String mode;
    protected String table;
    @GroupValue
    protected String group;
    protected List<Action<?>> actions;

    public ContractRequest() {
    }

    public ContractRequest(String mode, String table, String group, List<Action<?>> actions) {
        this.mode = mode;
        this.table = table;
        this.group = group;
        this.actions = actions;
    }

    public static class Action<T> {
        @FunctionValue
        protected String function;
        protected String key;
        protected T value;

        public Action() {
        }

        public Action(@FunctionValue String function, String key, T value) {
            this.function = function;
            this.key = key;
            this.value = value;
        }

    }

}
