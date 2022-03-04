package com.lu.code.foolish.egg.plugin;

import android.app.Dialog;
import android.view.View;
import android.view.Window;

import com.lu.code.foolish.egg.util.view.ViewUtil;
import com.lu.code.foolish.egg.util.log.LogUtil;

import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * @Author: Lu
 * Date: 2022/02/18
 * Description: fuck dialog
 */
public class FuckDialogPlugin extends BaseHookPlugin {

    public static class Config {
        public String matchText;
        public boolean regexMode;

        public Config(String matchText, boolean regexMode) {
            this.matchText = matchText;
            this.regexMode = regexMode;
        }

    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        alwaysHideDialog(lpparam);
    }

    /**
     * 不允许显示对话框
     *
     * @param lpparam
     */
    private void alwaysHideDialog(XC_LoadPackage.LoadPackageParam lpparam) {
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
                        if (ViewUtil.textCheck().findText(rootView, "更新|升级|安全警告|root")) {
                            needReplace = true;
                        }
                        if (needReplace) {
                            LogUtil.d(">>>find", needReplace);
                            //啥都不执行
                            return null;
                        }
                        return XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
                    }

                }
        );


    }
}
