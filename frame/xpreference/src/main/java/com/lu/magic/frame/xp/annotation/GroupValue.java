package com.lu.magic.frame.xp.annotation;


import static com.lu.magic.frame.xp.annotation.GroupValue.*;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


//1.指定注解的保留策略，RetentionPolicy.SOURCE表示只保留源码中，
//2.定义string值 ，它必需在 注解类型 前。
@Retention(RetentionPolicy.SOURCE)
@StringDef({GET, CONTAINS, COMMIT, APPLY})
public @interface GroupValue {
    String GET = "get";
    String CONTAINS = "contains";
    String COMMIT = "commit";
    String APPLY = "apply";
}
