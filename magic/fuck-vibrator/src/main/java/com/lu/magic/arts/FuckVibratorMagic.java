package com.lu.magic.arts;

import android.app.Service;

import com.lu.magic.bean.Config;
import com.lu.magic.util.AppUtil;
import com.lu.magic.config.ConfigUtil;
import com.lu.magic.config.ModuleId;
import com.lu.magic.util.log.LogUtil;

import org.json.JSONObject;

import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class FuckVibratorMagic extends BaseMagic {
    private Config<JSONObject> mConfig;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (mConfig == null) {
            mConfig = ConfigUtil.getBaseConfigCell(ModuleId.FUCK_VIBRATOR, lpparam.processName);
        }
        if (mConfig == null) {
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
                if (mConfig.isEnable()) {
                    LogUtil.d("已禁止震动：", lpparam.processName);
                    return null;
                }
                return XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
            }
        });

    }

}
