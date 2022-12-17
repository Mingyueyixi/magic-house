package com.lu.magic.arts;

import android.content.Context;
import android.util.Log;

import com.lu.magic.config.ConfigUtil;
import com.lu.magic.util.GsonUtil;
import com.lu.magic.util.ToastUtil;
import com.lu.magic.util.thread.AppExecutor;
import com.lu.magic.util.thread.WorkerUtil;

import java.util.Map;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class TestMagic extends BaseMagic {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        XposedHelpers.findAndHookMethod(lpparam.classLoader.loadClass("android.app.Activity"),
                "onResume",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        ConfigUtil.init((Context) param.thisObject);
                        handlePanQie();
                    }
                }
        );
    }

    private void handlePanQie() {
        AppExecutor.executeIO(() -> {
            try {
                Map<String, Object> data;
                data = ConfigUtil.getAll();
                Log.e("panqie", GsonUtil.toJson(data));
                AppExecutor.executeMain(() -> {
                    ToastUtil.show("嘎嘎----  " + GsonUtil.toJson(data));
                });
            } catch (Exception e) {
                AppExecutor.executeMain(() -> {
                    ToastUtil.show("嘎嘎----  " + Log.getStackTraceString(e));
                });
                Log.e("panqie", "", e);
            }
        });

    }


}
