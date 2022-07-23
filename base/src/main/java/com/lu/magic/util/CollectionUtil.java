package com.lu.magic.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CollectionUtil {

    public static void removeByIterator(List valueList, Object obj) {
        if (valueList == null) {
            return;
        }
        Iterator it = valueList.iterator();
        while (it.hasNext()) {
            Object ele = it.next();
            if (ele == null && obj == null) {
                it.remove();
            } else if (ele.equals(obj)) {
                it.remove();
            }
        }
    }

    public static <K, V> void removeByIterator(Map<K, V> kv, Object value) {
        Set<Map.Entry<K, V>> entrySet = kv.entrySet();
        Iterator<Map.Entry<K, V>> it = entrySet.iterator();
        while (it.hasNext()) {
            Map.Entry<K, V> ele = it.next();
            // K k = ele.getKey();
            V v = ele.getValue();
            if (value == null && v == null) {
                it.remove();
            } else if (v.equals(value)) {
                it.remove();
            }
        }
    }

    public static boolean isEmpty(Collection collection) {
        if (collection == null || collection.size() == 0) {
            return true;
        }
        return false;
    }

    public static boolean isNotEmpty(Collection collection) {
        return !isEmpty(collection);
    }

    public static boolean isEmpty(Map map) {
        if (map == null || map.size() == 0) {
            return true;
        }
        return false;
    }

    public static boolean isNotEmpty(Map map) {
        return !isEmpty(map);
    }

    public static boolean isEmptyArray(Object[] objects) {
        if (objects == null || objects.length == 0) {
            return true;
        }
        return false;
    }

    public static boolean isNotEmptyArray(Object[] objects) {
        return !isEmptyArray(objects);
    }
}
