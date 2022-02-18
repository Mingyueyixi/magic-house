package com.lu.code.foolish.egg.hook;

import android.app.AlertDialog;
import android.app.Dialog;
import android.view.View;
import android.view.Window;

import com.lu.code.foolish.egg.App;
import com.lu.code.foolish.egg.util.ViewUtil;
import com.lu.code.foolish.egg.util.log.LogUtil;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * @Author: Lu
 * Date: 2022/02/18
 * Description: fuck app update dialog
 */
public class FuckAppUpdateDialogPlugin extends BaseHookPlugin {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if ("com.chineseall.microbookroom".equals(lpparam.packageName)) {
            handleWeiShuFang(lpparam);
        }
    }

    private void handleWeiShuFang(XC_LoadPackage.LoadPackageParam lpparam) {
        XposedHelpers.findAndHookMethod("android.app.Dialog",
                lpparam.classLoader,
                "show",

                new XC_MethodReplacement() {
                    private Dialog dialog;
                    private boolean needReplace = false;

                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        if (needReplace) {
                            return null;
                        }

                        return null;
                    }

                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        dialog = (Dialog) param.thisObject;
                        Window window = dialog.getWindow();
                        View rootView = window.getDecorView();

                        if (ViewUtil.haveText(rootView, "更新")) {
                            needReplace = true;
                        }else {
//                            dialog.show();
                        }
                        LogUtil.d("hide微书房更新对话框:", needReplace);
                    }

                }
        );

    }
}
