package com.lu.magic.provider.annotation;

import static com.lu.magic.provider.annotation.ModeValue.*;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef(value = {READ, WRITE})
public @interface ModeValue {
    String READ = "read";
    String WRITE = "write";

}
