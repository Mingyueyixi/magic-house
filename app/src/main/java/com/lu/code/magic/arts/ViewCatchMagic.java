package com.lu.code.magic.arts;

import android.app.Activity;
import android.view.View;
import android.view.ViewParent;

import androidx.fragment.app.Fragment;

import com.lu.code.magic.bean.ViewCatchConfig;
import com.lu.code.magic.util.config.ConfigUtil;
import com.lu.code.magic.util.config.SheetName;
import com.lu.code.magic.util.log.LogUtil;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class ViewCatchMagic extends BaseMagic {
    private ViewCatchConfig config;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (config == null) {
            config = ConfigUtil.getCell(SheetName.VIEW_CATCH, lpparam.packageName, ViewCatchConfig.class);
        }
        if (config == null) {
            return;
        }

        if (config.getData() == null) {
            return;
        }
        handleActivity(lpparam);
        handleFragment(lpparam);
        handleView(lpparam);
    }

    private void handleView(XC_LoadPackage.LoadPackageParam lpparam) {
        if (!config.getData().viewClick) {
            return;
        }
        XposedHelpers.findAndHookMethod(
                View.class,
                "performClick",
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        View view = (View) param.thisObject;
                        ViewParent parent = view.getParent();
                        LogUtil.d("View performClick", getObjectDes(view), "child-->parent", getObjectDes(parent));
                    }

                }
        );

    }

    private void handleFragment(XC_LoadPackage.LoadPackageParam lpparam) {
        if (!config.getData().fragmentResume) {
            return;
        }

        XC_MethodHook hookFunc = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Object fragment = param.thisObject;
                Activity activity = (Activity) XposedHelpers.callMethod(fragment, "getActivity");
                LogUtil.d("Fragment onResume", getObjectDes(fragment), "Fragment-->Activity", getObjectDes(activity));
            }
        };

        XposedHelpers.findAndHookMethod(Fragment.class, "onResume", hookFunc);
        XposedHelpers.findAndHookMethod(android.app.Fragment.class, "onResume", hookFunc);
    }

    private static String getObjectDes(Object object) {
        if (object == null) {
            return "null";
        }
        return object.hashCode() + ":" +
                object.getClass().getName();
    }

    private void handleActivity(XC_LoadPackage.LoadPackageParam lpparam) {
        if (!config.getData().activityResume) {
            return;
        }
        XC_MethodHook hookFunc = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                LogUtil.d("Activity onResume", getObjectDes(param.thisObject));
            }
        };
        XposedHelpers.findAndHookMethod(Activity.class, "onResume", hookFunc);
    }
}
