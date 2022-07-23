package com.lu.magic.arts;

import android.location.LocationManager;

import com.lu.magic.bean.AMapConfig;
import com.lu.magic.util.config.ConfigUtil;
import com.lu.magic.util.log.LogUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Random;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class FuckAMapLocationMagic extends BaseMagic {
    private AMapConfig config;
    private Random mRandom = new Random();

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (config == null) {
            config = ConfigUtil.getAMapConfig(lpparam.packageName);
        }

        
        if (config == null || !config.isEnable()) {
            return;
        }

        LogUtil.d("fuck AMap Location");
        handleLocationService(lpparam);
        handleAMap(lpparam);
    }

    public void handleLocationService(XC_LoadPackage.LoadPackageParam lpparam) {
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

    }

    public void handleAMap(XC_LoadPackage.LoadPackageParam lpparam) throws ClassNotFoundException {
        //hook amap getLastKnownLocation
        XposedHelpers.findAndHookMethod(
                com.amap.api.location.AMapLocationClient.class,
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
        //hook amap setLocationListener
        XposedHelpers.findAndHookMethod(
                "com.amap.api.location.AMapLocationClient",
                lpparam.classLoader,
                "setLocationListener",
                lpparam.classLoader.loadClass("com.amap.api.location.AMapLocationListener"),
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        LogUtil.d("setLocationListener beforeHookedMethod");
//                        修改位置的两种方式：
//                        param.args[0] = proxyLocationListener(param.args[0]);
                        handlerLocationListenerMethod(lpparam, param);
                    }
                });
    }

    private void handlerLocationListenerMethod(XC_LoadPackage.LoadPackageParam lpparam, XC_MethodHook.MethodHookParam param) throws ClassNotFoundException {
        Object listener = param.args[0];
//        try {
//            //若xposed 的find函数找不到，直接反射
//            method = listener.getClass().getMethod("onLocationChanged", listener.getClass().getClassLoader().loadClass("com.amap.api.location.AMapLocation"));
//        } catch (NoSuchMethodException e) {
//            LogUtil.d(e);
//            return;
//        }
        Class<?> AMapLocationType = listener.getClass().getClassLoader().loadClass("com.amap.api.location.AMapLocation");
        Method method = XposedHelpers.findMethodExact(listener.getClass(), "onLocationChanged", AMapLocationType);
        if (method == null) {
            LogUtil.d("找不到onLocationChanged 函数");
            return;
        }
        LogUtil.d("hook method ", method);
        XposedBridge.hookMethod(method, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                super.beforeHookedMethod(param);
                changeLocationData(param.args[0]);
            }
        });
    }

    /**
     * 代理位置监听
     *
     * @param listener
     * @return
     */
    private Object proxyLocationListener(Object listener) {

        if (!Proxy.isProxyClass(listener.getClass())) {
            // 创建代理类
            return Proxy.newProxyInstance(
                    listener.getClass().getClassLoader(),
                    listener.getClass().getInterfaces(),
                    new AMapLocationListenerProxy(listener));
        }
        return listener;
    }

    /**
     * 位置监听代理，AMapLocationListener
     * 具有同样的classLoader
     */
    private final class AMapLocationListenerProxy implements InvocationHandler {
        private Object mListener;

        private AMapLocationListenerProxy(Object listener) {
            mListener = listener;
        }

        @Override
        public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
            if ("onLocationChanged".equals(method.getName())) {
                // 开始处理
                handlerLocationChanged(objects);
            }
            return method.invoke(mListener, objects);
        }

        private void handlerLocationChanged(Object[] objects) {
            if (objects == null || objects.length != 1) {
                return;
            }
            changeLocationData(objects[0]);
        }
    }

    /**
     * PS locationObj 是com.amap.api.location.AMapLocation对象。
     * 但是由于classloader可能不同，无法强转来使用
     *
     * @param locationObj
     */
    private void changeLocationData(Object locationObj) {
        double latitude = config.getLat();
        double longitude = config.getLng();

        if (latitude == 0 || longitude == 0) {
            return;
        }
        // 重新修改值
        int number = mRandom.nextInt(15 - 3 + 1) + 3;

        double lng = longitude + number / 100000d;
        double lat = latitude + number / 100000d;
        XposedHelpers.callMethod(locationObj, "setLongitude", lng);
        XposedHelpers.callMethod(locationObj, "setLatitude", lat);

        StringBuilder log = new StringBuilder("(" + latitude + " , " + longitude + ")")
                .append("--->")
                .append("(" + lat + " , " + lng + ")");
        LogUtil.d("changeLocationData", log);
    }
}
