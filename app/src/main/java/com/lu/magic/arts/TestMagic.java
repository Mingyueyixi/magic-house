package com.lu.magic.arts;

import android.util.Log;
import android.view.View;

import java.util.Map;

import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class TestMagic extends BaseMagic {
    private XSharedPreferences sp;
    private Map<String, ?> data;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
//        XposedHelpers.findAndHookMethod(
//                Activity.class,
//                "onResume",
//                new XC_MethodHook() {
//                    @Override
//                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                        super.afterHookedMethod(param);
//                        ToastUtil.show("尼玛resume");
//                    }
//                }
//
//        );

        
    }


}
