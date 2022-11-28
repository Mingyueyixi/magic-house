package com.lu.magic.util.config;

import static com.lu.magic.util.config.SheetName.*;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @Author: Lu
 * Date: 2022/04/06
 * Description:
 */
@Retention(RetentionPolicy.SOURCE)
@StringDef(value = {
        EMPTY,
        FUCK_DIALOG,
        AMAP_LOCATION,
        FUCK_VIBRATOR,
        FUCK_SCREEN_ORIENTATION,
        VIEW_CATCH,
        VIEW_LOCK
})
public @interface SheetName {
    String EMPTY = "";
    String FUCK_DIALOG = "FuckDialog";
    String AMAP_LOCATION = "AMapLocation";
    String FUCK_VIBRATOR = "FuckVibrator";
    String FUCK_SCREEN_ORIENTATION = "fuckScreenRotate";
    String VIEW_CATCH = "ViewCatch";
    String VIEW_LOCK = "ViewLock";
}
