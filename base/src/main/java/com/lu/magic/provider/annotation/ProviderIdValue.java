package com.lu.magic.provider.annotation;


import static com.lu.magic.provider.annotation.ProviderIdValue.MMKV;
import static com.lu.magic.provider.annotation.ProviderIdValue.SP;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef(value = {MMKV, SP})
public @interface ProviderIdValue {
    String MMKV = "MMKV";
    String SP = "SP";

}
