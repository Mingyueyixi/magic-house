package com.lu.code.foolish.egg.hook;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class TestHookPlugin extends BaseHookPlugin {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        switch (lpparam.packageName) {
            case "com.lu.code.fox":
                hookFoxApp(lpparam);
                break;
            case "com.snda.wifilocating":
                hookFoxApp(lpparam);
                break;
            default:
                break;
        }
    }

    private void hookFoxApp(XC_LoadPackage.LoadPackageParam lpparam) {
        XposedHelpers.findAndHookMethod(
                "android.app.Activity",
                lpparam.classLoader,
                "onCreate",
                Bundle.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        XposedBridge.log( "尼瑪");
                        Log.e(">>>", "尼玛。。。");
                        Object toast = XposedHelpers.callStaticMethod(
                                lpparam.classLoader.loadClass("android.widget.Toast"),
                                "makeText",
                                (Activity) param.thisObject,
                                "尼玛",
                                Toast.LENGTH_LONG);
                        XposedHelpers.callMethod(toast, "show");
                    }
                });
    }
}
