package com.lu.magic.frame.xp.provider;

import com.lu.magic.frame.xp.provider.annotation.FunctionValue;
import com.lu.magic.frame.xp.provider.annotation.GroupValue;
import com.lu.magic.frame.xp.provider.annotation.PreferenceIdValue;

import java.util.List;

/**
 * ContentProvider约定的数据结构
 */
public class ContractRequest {
    @PreferenceIdValue
    protected String preferenceId;
    protected String mode;
    protected String table;
    @GroupValue
    protected String group;
    protected List<Action<?>> actions;

    public ContractRequest() {
    }

    public ContractRequest(String preferenceId, String mode, String table, String group, List<Action<?>> actions) {
        this.preferenceId = preferenceId;
        this.mode = mode;
        this.table = table;
        this.group = group;
        this.actions = actions;
    }

    public static class Action<T> {
        @FunctionValue
        public String function;
        public String key;
        public T value;

        public Action() {
        }

        public Action(@FunctionValue String function, String key, T value) {
            this.function = function;
            this.key = key;
            this.value = value;
        }

    }

}
