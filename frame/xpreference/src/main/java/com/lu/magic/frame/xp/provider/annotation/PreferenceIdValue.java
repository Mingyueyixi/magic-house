package com.lu.magic.frame.xp.provider.annotation;


import static com.lu.magic.frame.xp.provider.annotation.PreferenceIdValue.*;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef(value = {MMKV, SP})
public @interface PreferenceIdValue {
    String MMKV = "MMKV";
    String SP = "SP";

}
