package com.lu.code.magic.arts;

import android.app.Activity;
import android.content.pm.ActivityInfo;

import com.lu.code.magic.bean.BaseConfig;
import com.lu.code.magic.util.config.ConfigUtil;
import com.lu.code.magic.util.config.SheetName;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class FuckScreenMagic extends BaseMagic {
    private BaseConfig config;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (config == null) {
            ConfigUtil.getCell(SheetName.FUCK_SCREEN_ORIENTATION, lpparam.packageName, BaseConfig.class);
        }
        if (config == null || !config.isEnable()) {
            return;
        }
        handleMagic(lpparam);
    }

    private void handleMagic(XC_LoadPackage.LoadPackageParam lpparam) {
        XposedHelpers.findAndHookMethod(Activity.class,
                "setRequestedOrientation",
                int.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        int requestedOrientation = (int) param.args[0];
                        if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR) {

                        }
                    }
                });
    }
}
