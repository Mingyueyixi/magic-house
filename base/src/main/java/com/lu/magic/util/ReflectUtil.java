package com.lu.magic.util;

import com.lu.magic.util.log.LogUtil;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * @Author Lu
 * 简易反射工具类
 */
public class ReflectUtil {
    /**
     * 合并数组
     *
     * @param array1
     * @param array2
     * @return
     */
    public static Object combineArray(Object array1, Object array2) {
        Class<?> elementType = array1.getClass().getComponentType();
        int len1 = Array.getLength(array1);
        int len2 = len1 + Array.getLength(array2);
        Object result = Array.newInstance(elementType, len2);
        for (int k = 0; k < len2; ++k) {
            if (k < len1) {
                Array.set(result, k, Array.get(array1, k));
            } else {
                Array.set(result, k, Array.get(array2, k - len1));
            }
        }
        return result;
    }

    public static Object getFieldValue(Object obj, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        return getFieldValue(obj, obj.getClass(), fieldName);
    }

    public static Object getFieldValue(Object obj, Class cls, String fieldValue) throws NoSuchFieldException, IllegalAccessException {
        Field localField;
        try {
            localField = cls.getField(fieldValue);
        } catch (NoSuchFieldException e) {
            localField = cls.getDeclaredField(fieldValue);
        }
        LogUtil.d(cls.getName() + " getField  " + localField.getName());
        localField.setAccessible(true);
        return localField.get(obj);
    }

    public static Method getMatchingMethod(Class<?> aClass, String name, Class<?>... params) {
        Method method = null;
        try {
            method = aClass.getMethod(name, params);
        } catch (NoSuchMethodException e) {
            try {
                method = aClass.getDeclaredMethod(name, params);
            } catch (NoSuchMethodException noSuchMethodException) {
                noSuchMethodException.printStackTrace();
            }
        }
        return method;
    }

    public static Object invokeConstructor(Class<?> aClass, Object... args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        Constructor<?> conor = getConstructor(aClass, args);
        if (!conor.isAccessible()) {
            conor.setAccessible(true);
        }
        return conor.newInstance(args);
    }

    public static Object invokeMethod(Object instance, String methodName, Object... args) {
        ArrayList<Class<?>> paramTypes = new ArrayList<>();
        for (Object arg : args) {
            paramTypes.add(arg.getClass());
        }
        Class<?>[] array = paramTypes.toArray(new Class[paramTypes.size()]);
        Method method = getMatchingMethod(instance.getClass(), methodName, array);
        try {
            return method.invoke(instance, args);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Constructor<?> getConstructor(Class<?> aClass, Object... args) throws NoSuchMethodException {
        Class<?>[] parameterTypes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            parameterTypes[i] = args[i].getClass();
        }
        return aClass.getConstructor(parameterTypes);
    }

}