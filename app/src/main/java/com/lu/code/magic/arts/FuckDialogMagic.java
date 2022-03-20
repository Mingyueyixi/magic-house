package com.lu.code.magic.arts;

import android.app.Dialog;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.PopupWindow;

import com.lu.code.magic.util.view.ViewUtil;
import com.lu.code.magic.util.log.LogUtil;

import java.lang.reflect.InvocationTargetException;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * @Author: Lu
 * Date: 2022/02/18
 * Description: fuck dialog
 */
public class FuckDialogMagic extends BaseMagic {

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
                        LogUtil.d(">>>", lpparam.processName, "will show a dialog");
                        if (ViewUtil.textCheck().findText(rootView, "更新|升级|安全警告|root|继续使用QQ")) {
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

        XposedHelpers.findAndHookMethod(PopupWindow.class,
                "showAtLocation",
                IBinder.class,
                int.class,
                int.class,
                int.class,
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        return replacePopupWindow(lpparam, param);
                    }

                }
        );

        XposedHelpers.findAndHookMethod(
                PopupWindow.class,
                "showAsDropDown",
                View.class,
                int.class,
                int.class,
                int.class,
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        return replacePopupWindow(lpparam, param);
                    }
                }
        );

    }

    private Object replacePopupWindow(XC_LoadPackage.LoadPackageParam lpparam, XC_MethodHook.MethodHookParam param) throws InvocationTargetException, IllegalAccessException {
        PopupWindow popupWindow = (PopupWindow) param.thisObject;
        View rootView = (View) XposedHelpers.getObjectField(popupWindow, "mDecorView");
        boolean needReplace = false;
        LogUtil.d(">>>", lpparam.processName, "will show a PopupWindow");
        if (ViewUtil.textCheck().findText(rootView, "更新|升级|安全警告|root|继续使用QQ")) {
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
