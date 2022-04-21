package com.lu.code.magic.util.config;

import static com.lu.code.magic.util.config.SheetName.AMAP_LOCATION;
import static com.lu.code.magic.util.config.SheetName.FUCK_DIALOG;
import static com.lu.code.magic.util.config.SheetName.FUCK_VIBRATOR;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @Author: Lu
 * Date: 2022/04/06
 * Description:
 */
@Retention(RetentionPolicy.SOURCE)
@StringDef(value = {FUCK_DIALOG, AMAP_LOCATION, FUCK_VIBRATOR})
public @interface SheetName {
    String FUCK_DIALOG = "FuckDialog";
    String AMAP_LOCATION = "AMapLocation";
    String FUCK_VIBRATOR = "FuckVibrator";
}
