package com.lu.magic.arts;

import android.content.Context;
import android.os.Bundle;

import com.lu.magic.bean.Config;
import com.lu.magic.screen.OrientationDTO;
import com.lu.magic.screen.ScreenOrientationUtil;
import com.lu.magic.config.ConfigUtil;
import com.lu.magic.config.ModuleId;
import com.lu.magic.util.log.LogUtil;

import org.json.JSONObject;

import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class FuckScreenMagic extends BaseMagic {

    private Config<OrientationDTO> config;

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

    private Config<OrientationDTO> loadConfig(String pkgName) {
        JSONObject json = ConfigUtil.getCell(ModuleId.FUCK_SCREEN_ORIENTATION, pkgName);
        return Config.fromJson(json, OrientationDTO::fromJson);
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
                        try {
                            LogUtil.d("setRequestedOrientation beforeHookedMethod" + param.args[0]);
                            throw new Exception("setRequestedOrientation");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

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
