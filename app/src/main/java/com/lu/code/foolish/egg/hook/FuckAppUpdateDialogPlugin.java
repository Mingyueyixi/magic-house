package com.lu.code.foolish.egg.hook;

import android.app.Dialog;
import android.view.View;
import android.view.Window;

import com.lu.code.foolish.egg.util.ViewUtil;
import com.lu.code.foolish.egg.util.log.LogUtil;

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
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        Dialog dialog = (Dialog) param.thisObject;
                        Window window = dialog.getWindow();
                        View rootView = window.getDecorView();
                        boolean needReplace = false;
                        if (ViewUtil.haveText(rootView, "更新")) {
                            needReplace = true;
                        }
                        LogUtil.d("hide微书房更新对话框:", needReplace);
                        if (needReplace) {
                            //啥都不执行
                            return null;
                        }
                        return XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
                    }

                }
        );

    }
}
