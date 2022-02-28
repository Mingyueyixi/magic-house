package com.lu.code.foolish.egg.hook;

import android.app.Dialog;
import android.view.View;
import android.view.Window;

import com.lu.code.foolish.egg.util.Select;
import com.lu.code.foolish.egg.util.ViewUtil;
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
        handleWeiShuFang(lpparam);
        handleLog(lpparam);
    }

    private void handleLog(XC_LoadPackage.LoadPackageParam lpparam) {
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
                        //https://tieba.baidu.com/p/7034968938
                        if (ViewUtil.haveText(rootView, "安全警告")) {
                            needReplace = true;
                        }
                        LogUtil.d("check日志对话框:", needReplace);
                        if (needReplace) {
                            //啥都不执行
//                            return null;
                        }
                        return XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
                    }

                }
        );
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
