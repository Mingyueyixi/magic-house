package com.lu.code.magic.util;

import java.util.HashMap;

public class SingleStoreUtil {
    private static class SingleHolder {
        private static final Store store = new Store();
    }

    public static void remove(Class<?> key) {
        SingleHolder.store.remove(key);
    }

    public static void put(Object obj) {
        SingleHolder.store.put(obj);
    }

    public static <T> T get(Class<T> key) {
        return SingleHolder.store.get(key);
    }

    private static class Store {
        private HashMap<String, Object> modelStoreMap = new HashMap<>();

        public void remove(Class<?> key) {
            if (key == null) {
                return;
            }
            modelStoreMap.remove(key.toString());
        }

        public void put(Object obj) {
            if (obj == null) {
                return;
            }
            modelStoreMap.put(obj.getClass().toString(), obj);
        }

        public <T> T get(Class<T> key) {
            Object v = modelStoreMap.get(key.toString());
            if (v == null) {
                return null;
            }
            return key.cast(v);
        }
    }
}
