package com.lu.magic.config;

import static com.lu.magic.config.ModuleId.*;

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
        DEVELOP_CATCH_LOG,
        VIEW_LOCK
})
public @interface ModuleId {
    String EMPTY = "";
    String FUCK_DIALOG = "FuckDialog";
    String AMAP_LOCATION = "AMapLocation";
    String FUCK_VIBRATOR = "FuckVibrator";
    String FUCK_SCREEN_ORIENTATION = "fuckScreenRotate";
    String DEVELOP_CATCH_LOG = "DevCatchLog";
    String VIEW_LOCK = "ViewLock";
}
