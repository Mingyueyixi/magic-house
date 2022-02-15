package com.lu.code.foolish.egg.hook;

import android.os.Build;

import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class DisableFlagSecurePlugin extends BaseHookPlugin {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (loadPackageParam.packageName.equals("android")) {
            try {
                Class<?> windowsState = XposedHelpers.findClass("com.android.server.wm.WindowState", loadPackageParam.classLoader);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    XposedHelpers.findAndHookMethod(
                            windowsState,
                            "isSecureLocked",
                            XC_MethodReplacement.returnConstant(false));
                } else {
                    XposedHelpers.findAndHookMethod(
                            "com.android.server.wm.WindowManagerService",
                            loadPackageParam.classLoader,
                            "isSecureLocked",
                            windowsState,
                            XC_MethodReplacement.returnConstant(false));
                }
            } catch (Throwable t) {
                XposedBridge.log(t);
            }
        }
    }
}
