package com.lu.code.magic.arts;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;

import com.lu.code.magic.bean.BaseConfig;
import com.lu.code.magic.main.screen.OrientationDTO;
import com.lu.code.magic.main.screen.ScreenOrientationUtil;
import com.lu.code.magic.provider.XPreference;
import com.lu.code.magic.util.GsonUtil;
import com.lu.code.magic.util.config.ConfigUtil;
import com.lu.code.magic.util.config.SheetName;
import com.lu.code.magic.util.log.LogUtil;

import java.lang.reflect.Type;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class FuckScreenMagic extends BaseMagic {

    private BaseConfig<OrientationDTO> config;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (config == null) {
            config = loadConfig(lpparam.packageName);
        }
        if (config == null || !config.isEnable()) {
            return;
        }
        OrientationDTO data = config.getData();
        List<OrientationDTO.ActItem> actList = data.getActList();
        for (OrientationDTO.ActItem actItem : actList) {
            handleMagic(lpparam, actItem);
        }

    }

    private BaseConfig<OrientationDTO> loadConfig(String pkgName) {
        Type configType = GsonUtil.getType(BaseConfig.class, OrientationDTO.class);
        return ConfigUtil.getCellForType(SheetName.FUCK_SCREEN_ORIENTATION, pkgName, configType);
    }

    private void handleMagic(XC_LoadPackage.LoadPackageParam lpparam, OrientationDTO.ActItem actItem) {

        XposedHelpers.findAndHookMethod(
                actItem.getActClass(),
                lpparam.classLoader,
                "onCreate",
                Bundle.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        int currOrientation = (int) XposedHelpers.callMethod(param.thisObject, "getRequestedOrientation");
                        if (currOrientation == actItem.getOrientation()) {
                            return;
                        }
                        XposedHelpers.callMethod(param.thisObject, "setRequestedOrientation", actItem.getOrientation());
                        LogUtil.d("设置：",
                                param.thisObject.getClass().getName(),
                                "的方向:",
                                actItem.getOrientation());
                    }
                });
        XposedHelpers.findAndHookMethod(
                actItem.getActClass(),
                lpparam.classLoader,
                "setRequestedOrientation",
                int.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        int requestedOrientation = (int) param.args[0];
                        param.args[0] = actItem.getOrientation();

                        try {
                            Context context = (Context) param.thisObject;
                            String textId = ScreenOrientationUtil.getText(context, requestedOrientation);
                            LogUtil.d(textId);
                        } catch (Exception e) {
                            LogUtil.e(e);
                        }
                    }
                });

    }
}
