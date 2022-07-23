package com.lu.magic.arts;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.lu.magic.util.AppUtil;
import com.lu.magic.util.ReflectUtil;
import com.lu.magic.util.log.LogUtil;
import com.lu.magic.util.view.ViewUtil;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class TestMagic extends BaseMagic {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        switch (lpparam.packageName) {
            case "com.lu.code.fox":
                hookFoxApp(lpparam);
                break;
            case "com.snda.wifilocating":
                hookFoxApp(lpparam);
                break;
            case "com.i61.ailesson":
                hookGubi(lpparam);
                break;
            default:
                break;
        }
    }

    private void hookGubi(XC_LoadPackage.LoadPackageParam lpparam) throws ClassNotFoundException {
//        String className = "com.i61.ailesson.mvp.ui.activity";
//        replaceMethod(lpparam, className,"loadMoreComplete");
//        replaceMethod(lpparam, className,"refreshComplete");
//        replaceMethod(lpparam, className,"resetNoMoreData");
        Class<?> clzz = lpparam.classLoader.loadClass("com.i61.ailesson.cms.adapters.CmsLiveCardIngAdapter");

        XposedBridge.hookAllConstructors(
                clzz,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        Object vh = ReflectUtil.getFieldValue(param.thisObject, "vh");
                        LogUtil.d("cms Live adapter new >>>===<<<", param.thisObject, "     ------  ", vh);
                    }
                }
        );

        XposedBridge.hookAllMethods(
                clzz,
                "onBindViewHolder",
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        Object vh = ReflectUtil.getFieldValue(param.thisObject, "vh");
                        LogUtil.d("cms Live adapter bind >>>===<<<", param.thisObject, "     ------  ", vh);
                    }
                });
        hookFoxApp(lpparam);
    }

    private void replaceMethod(XC_LoadPackage.LoadPackageParam lpparam, String className, String method) {
        XposedHelpers.findAndHookMethod(
                className,
                lpparam.classLoader,
                method,
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        return null;
                    }
                }
        );
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
                        XposedBridge.log("尼瑪");
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
