package com.lu.magic;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;

import com.lu.magic.util.AppUtil;
import com.lu.magic.util.ToastUtil;
import com.lu.magic.util.log.LogUtil;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Main implements IXposedHookLoadPackage {
    private boolean haveBean = false;
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XposedHelpers.findAndHookMethod(
                Activity.class,
                "onCreate",
                Bundle.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        if (haveBean) {
                            return;
                        }
                        haveBean = true;
                        AppUtil.attachContext(((Context) param.thisObject).getApplicationContext());
                        hookFeature(lpparam);
                    }
                });
    }

    public void hookFeature(XC_LoadPackage.LoadPackageParam lpparam) {
        XposedHelpers.findAndHookMethod(
                Activity.class,
                "onResume",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        ToastUtil.show("尼玛" + param.thisObject);
                    }
                }
        );
        handleLocationService(lpparam);
        hookLast(lpparam);
        hookSetListener(lpparam);
    }

    private void hookSetListener(XC_LoadPackage.LoadPackageParam lpparm) {
        //hook amap setLocationListener
        try {
            XposedHelpers.findAndHookMethod(
                    "com.amap.api.location.AMapLocationClient",
                    AppUtil.getContext().getClassLoader(),
                    "setLocationListener",
                    AppUtil.getContext().getClassLoader().loadClass("com.amap.api.location.AMapLocationListener"),
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            LogUtil.i("setLocationListener beforeHookedMethod");
                        }
                    });
        } catch (Throwable e) {
            LogUtil.e("尼玛的", e);
        }

    }

    private void hookLast(XC_LoadPackage.LoadPackageParam lpparm) {
        try {
            XposedHelpers.findAndHookMethod(
                    "com.amap.api.location.AMapLocationClient",
                    AppUtil.getContext().getClassLoader(),
                    "getLastKnownLocation",
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            //                        AMapLocation location = (AMapLocation) param.thisObject;
                            LogUtil.d("getLastKnownLocation");
                            param.setResult(null);
                        }
                    }
            );
        } catch (Exception e) {
            LogUtil.e("尼玛的", e);
        }
    }


    public void handleLocationService(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            //定位服务开关，即使没开，也改为打开
            XposedHelpers.findAndHookMethod(LocationManager.class,
                    "isProviderEnabled",
                    String.class, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            LogUtil.d("isProviderEnabled");
                            param.setResult(true);
                        }
                    });
        } catch (Exception e) {
            LogUtil.e("尼玛的", e);
        }


    }

}