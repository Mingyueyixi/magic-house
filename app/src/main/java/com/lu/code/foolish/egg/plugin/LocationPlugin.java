package com.lu.code.foolish.egg.plugin;

import android.content.ContentResolver;
import android.location.LocationManager;
import android.provider.Settings;
import android.text.TextUtils;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * @Author: Lu
 * Date: 2022/03/03
 * Description:
 */
public class LocationPlugin extends BaseHookPlugin {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        hookGPSProviderStatus();
    }

    /**
     * hook 标准 gps isProviderEnabled 所有定位都会判断 GPS 开关状态
     */
    private void hookGPSProviderStatus() {
        //通用 hook GPS为打开状态
        XposedHelpers.findAndHookMethod(
                LocationManager.class,
                "isProviderEnabled",
                String.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                        LocationManager.GPS_PROVIDER
                        if ("gps".equals(param.args[0])) {
                            param.setResult(true);
                        }
                    }
                });

        //其他检测方法，android s开始不存在LOCATION_PROVIDERS_ALLOWED
        //Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        XposedHelpers.findAndHookMethod(Settings.Secure.class,
                "getString",
                ContentResolver.class,
                String.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        String secureFlag = (String) param.args[1];
                        //如果是检测位置是否允许
                        if ("location_providers_allowed".equals(secureFlag)) {
                            String result = String.valueOf(param.getResult());
                            if (TextUtils.isEmpty(result)) {
                                param.setResult("gps,network");
                            }
                        }
                    }
                });
    }

}
