package com.lu.magic.module;

import com.lu.magic.arts.BaseMagic;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * @Author: Lu
 * Date: 2022/02/21
 * Description:
 */
public class MagicSelfEntry extends BaseMagic {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        changeHookModuleState(lpparam);
    }

    private void changeHookModuleState(XC_LoadPackage.LoadPackageParam lpparam) {
        //不能直接使用class 来hook
        XposedHelpers.findAndHookMethod(
                "com.lu.magic.module.AppInitProxy",
                lpparam.classLoader,
                "isActiveHookModule",
                XC_MethodReplacement.returnConstant(true)
        );
        //这里不能直接调用来验证
        //XposedBridge.log(AppInitProxy.isActiveHookModule());
    }


}
