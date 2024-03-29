package com.lu.magic.screen;

import android.content.Context;
import android.content.pm.ActivityInfo;

import com.lu.magic.util.log.LogUtil;

public class ScreenOrientationUtil {

    public static int getText(int mode) {
        int subText = -1;
        switch (mode) {
            case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
                subText = com.lu.magic.base.R.string.screen_orientation_landscape;
                break;
            case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                subText = com.lu.magic.base.R.string.screen_orientation_portrait;
                break;
            case ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE:
                subText = com.lu.magic.base.R.string.screen_orientation_reverse_landscape;
                break;
            case ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT:
                subText = com.lu.magic.base.R.string.screen_orientation_reverse_portrait;
                break;
            case ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT:
                subText = com.lu.magic.base.R.string.screen_orientation_sensor_portrait;
                break;
            case ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE:
                subText = com.lu.magic.base.R.string.screen_orientation_sensor_landscape;
                break;
            case ActivityInfo.SCREEN_ORIENTATION_SENSOR:
                subText = com.lu.magic.base.R.string.screen_orientation_sensor;
                break;
            case ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR:
                subText = com.lu.magic.base.R.string.screen_orientation_full_sensor;
                break;
            case ActivityInfo.SCREEN_ORIENTATION_NOSENSOR:
                subText = com.lu.magic.base.R.string.screen_orientation_nosensor;
                break;
            case ActivityInfo.SCREEN_ORIENTATION_USER:
                subText = com.lu.magic.base.R.string.screen_orientation_user;
                break;
            case ActivityInfo.SCREEN_ORIENTATION_FULL_USER:
                subText = com.lu.magic.base.R.string.screen_orientation_full_user;
                break;
            case ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT:
                subText = com.lu.magic.base.R.string.screen_orientation_user_portrait;
                break;
            case ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE:
                subText = com.lu.magic.base.R.string.screen_orientation_user_landscape;
                break;
            case ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED:
                subText = com.lu.magic.base.R.string.screen_orientation_unspecified;
                break;
            default:
                LogUtil.d("未实现");
                break;
        }
        return subText;
    }

    public static String getText(Context context, int mode) {
        int textId = getText(mode);
        if (textId == -1) {
            return "";
        }
        return context.getString(textId);
    }
}
