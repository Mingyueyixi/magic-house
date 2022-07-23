package com.lu.magic.util;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Lu
 * Date: 2022/03/08
 * Description:
 */
public final class LiveDataBus {

    private final Map<String, MutableLiveData<Object>> bus;

    private LiveDataBus() {
        bus = new HashMap<>();
    }

    private static class SingletonHolder {
        private static final LiveDataBus DATA_BUS = new LiveDataBus();
    }

    public static LiveDataBus get() {
        return SingletonHolder.DATA_BUS;
    }

    public <T> MutableLiveData<T> with(String target, Class<T> type) {
        if (!bus.containsKey(target)) {
            bus.put(target, new MutableLiveData<>());
        }
        return (MutableLiveData<T>) bus.get(target);
    }

    public MutableLiveData<Object> with(String target) {
        return with(target, Object.class);
    }

}