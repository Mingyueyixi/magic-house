package com.lu.magic.frame.xp.bean;

import androidx.annotation.Keep;

import com.lu.magic.frame.xp.annotation.FunctionValue;
import com.lu.magic.frame.xp.annotation.GroupValue;
import com.lu.magic.frame.xp.annotation.PreferenceIdValue;
import com.lu.magic.frame.xp.util.Ids;

import java.util.List;

/**
 * ContentProvider约定的数据结构
 */
@Keep
public class ContractRequest {
    @PreferenceIdValue
    public String preferenceId;
    public String mode;
    public String table;
    @GroupValue
    public String group;
    public List<Action<?>> actions;

    public String requestId;

    public ContractRequest() {
    }

    static String genRequestId() {
        return Ids.Companion.genRequestId();
    }

    public ContractRequest(String preferenceId, String mode, String table, String group, List<Action<?>> actions) {
        this.preferenceId = preferenceId;
        this.mode = mode;
        this.table = table;
        this.group = group;
        this.actions = actions;
        this.requestId = genRequestId();
    }

    @Keep
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
