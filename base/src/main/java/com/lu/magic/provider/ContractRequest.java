package com.lu.magic.provider;

import com.lu.magic.provider.annotation.FunctionValue;
import com.lu.magic.provider.annotation.GroupValue;
import com.lu.magic.provider.annotation.ProviderIdValue;

import java.util.List;

/**
 * ContentProvider约定的数据结构
 */
public class ContractRequest {
    @ProviderIdValue
    protected String providerId;
    protected String mode;
    protected String table;
    @GroupValue
    protected String group;
    protected List<Action<?>> actions;

    public ContractRequest() {
    }

    public ContractRequest(String providerId, String mode, String table, String group, List<Action<?>> actions) {
        this.providerId = providerId;
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
