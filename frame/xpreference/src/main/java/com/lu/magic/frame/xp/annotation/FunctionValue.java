package com.lu.magic.frame.xp.annotation;


import static com.lu.magic.frame.xp.annotation.FunctionValue.*;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

//1.指定注解的保留策略，RetentionPolicy.SOURCE表示只保留源码中，
//2.定义string值 ，它必需在 注解类型 前。
@Retention(RetentionPolicy.SOURCE)
@StringDef(value = {GET_STRING, GET_ALL, GET_BOOLEAN, GET_INT, GET_STRING_SET, GET_FLOAT, GET_LONG, CONTAINS, PUT_STRING, PUT_BOOLEAN, PUT_INT, PUT_STRING_SET, PUT_FLOAT, PUT_LONG, REMOVE, CLEAR})
public @interface FunctionValue {
    String GET_STRING = "getString";
    String GET_BOOLEAN = "getBoolean";
    String GET_INT = "getInt";
    String GET_STRING_SET = "getStringSet";
    String GET_FLOAT = "getFloat";
    String GET_LONG = "getLong";
    String GET_ALL = "getAll";

    String CONTAINS = "contains";

    String PUT_STRING = "putString";
    String PUT_BOOLEAN = "putBoolean";
    String PUT_INT = "putInt";
    String PUT_STRING_SET = "putStringSet";
    String PUT_FLOAT = "putFloat";
    String PUT_LONG = "putLong";

    String REMOVE = "remove";
    String CLEAR = "clear";

}
