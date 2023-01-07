package com.lu.magic.arts;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.lu.magic.config.ConfigUtil;
import com.lu.magic.frame.xp.CPPreference;
import com.lu.magic.util.AppUtil;
import com.lu.magic.util.GsonUtil;
import com.lu.magic.util.ToastUtil;
import com.lu.magic.util.log.LogUtil;
import com.lu.magic.util.thread.AppExecutor;
import com.lu.magic.util.thread.WorkerUtil;

import java.io.File;
import java.util.Map;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
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

    private void handlePanQie() {

    }


}
