package com.lu.code.magic.arts;

import android.app.Service;

import com.lu.code.magic.bean.BaseConfig;
import com.lu.code.magic.util.AppUtil;
import com.lu.code.magic.util.config.ConfigUtil;
import com.lu.code.magic.util.config.SheetName;
import com.lu.code.magic.util.log.LogUtil;

import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class FuckVibratorMagic extends BaseMagic {
    private BaseConfig config;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (config == null) {
            config = ConfigUtil.getCell(SheetName.FUCK_VIBRATOR, lpparam.processName, BaseConfig.class);
        }
        if (config == null) {
            return;
        }
        if (!lpparam.processName.equals(lpparam.packageName)) {
            return;
        }
        handleMagic(lpparam);
    }


    private void handleMagic(XC_LoadPackage.LoadPackageParam lpparam) {
        Object vibratorService = null;
        try {
            vibratorService = AppUtil.getContext().getSystemService(Service.VIBRATOR_SERVICE);
        } catch (Exception e) {
            //可能不具备震动权限
            LogUtil.e(e);
        }
        if (vibratorService == null) {
            LogUtil.d("无法获取振动器！！！");
            return;
        }
        XposedBridge.hookAllMethods(vibratorService.getClass(), "vibrate", new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                //TODO:抖音app不知道搞了啥，hook不到
                //震动器配置有效
                if (config.isEnable()) {
                    LogUtil.d("已禁止震动：", lpparam.processName);
                    return null;
                }
                return XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
            }
        });

    }

}
