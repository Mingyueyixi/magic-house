package com.lu.code.magic.arts;

import android.app.Dialog;
import android.os.IBinder;
import android.view.View;
import android.view.Window;
import android.widget.PopupWindow;

import com.lu.code.magic.bean.FuckDialogConfig;
import com.lu.code.magic.util.GsonUtil;
import com.lu.code.magic.util.config.ConfigUtil;
import com.lu.code.magic.util.log.LogUtil;
import com.lu.code.magic.util.view.ViewUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

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

    private FuckDialogConfig mFuckDialogConfig;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (mFuckDialogConfig == null) {
            handleLoadConfig(lpparam);
        }
        LogUtil.d(lpparam.packageName, lpparam.processName, "FuckDialog配置:", GsonUtil.toJson(mFuckDialogConfig));
        if (mFuckDialogConfig == null || !mFuckDialogConfig.isEnable()) {
            return;
        }
        hideDialog(lpparam);
        hidePopupWindow(lpparam);
    }

    private void handleLoadConfig(XC_LoadPackage.LoadPackageParam lpparam) {
        Map<String, FuckDialogConfig> configMap = ConfigUtil.getFuckDialogConfigAll();
        FuckDialogConfig config = configMap.get(lpparam.packageName);
        if (config == null) {
            config = configMap.get(lpparam.processName);
        }
        if (config == null) {
            int index = lpparam.processName.indexOf(":");
            if (index > -1) {
                String packageName = lpparam.processName.substring(0, index);
                configMap.get(packageName);
            }
        }
        mFuckDialogConfig = config;
    }

    /**
     * 不允许显示对话框
     *
     * @param lpparam
     */
    private void hideDialog(XC_LoadPackage.LoadPackageParam lpparam) {
        XposedHelpers.findAndHookMethod("android.app.Dialog",
                lpparam.classLoader,
                "show",
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        return replaceDialogShowMethod(lpparam, param);
                    }

                }
        );

    }

    private void hidePopupWindow(XC_LoadPackage.LoadPackageParam lpparam) {
        XposedHelpers.findAndHookMethod(PopupWindow.class,
                "showAtLocation",
                IBinder.class,
                int.class,
                int.class,
                int.class,
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        return replacePopupWindowShowMethod(lpparam, param);
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
                        return replacePopupWindowShowMethod(lpparam, param);
                    }
                }
        );


    }

    private Object replacePopupWindowShowMethod(XC_LoadPackage.LoadPackageParam lpparam, XC_MethodHook.MethodHookParam param) throws InvocationTargetException, IllegalAccessException {
        PopupWindow popupWindow = (PopupWindow) param.thisObject;
        View rootView = (View) XposedHelpers.getObjectField(popupWindow, "mDecorView");
        boolean needReplace = false;
        LogUtil.d(">>>", lpparam.processName, "will show a PopupWindow");

        if ("regex".equals(mFuckDialogConfig.getMode())) {
            needReplace = ViewUtil.textCheck().findText(rootView, mFuckDialogConfig.getKeyword());
        } else {
            needReplace = ViewUtil.textCheck().haveText(rootView, mFuckDialogConfig.getKeyword());
        }
        if (needReplace) {
            LogUtil.d(">>>find", needReplace);
            popupWindow.setOnDismissListener(null);
            //啥都不执行
            return null;
        }
        return XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
    }

    private Object replaceDialogShowMethod(XC_LoadPackage.LoadPackageParam lpparam, XC_MethodHook.MethodHookParam param) throws InvocationTargetException, IllegalAccessException {
        Dialog dialog = (Dialog) param.thisObject;
        Window window = dialog.getWindow();
        View rootView = window.getDecorView();
        boolean needReplace = false;
        LogUtil.d(lpparam.processName, "will show a dialog");
        if ("regex".equals(mFuckDialogConfig.getMode())) {
            needReplace = ViewUtil.textCheck().findText(rootView, mFuckDialogConfig.getKeyword());
        } else {
            needReplace = ViewUtil.textCheck().haveText(rootView, mFuckDialogConfig.getKeyword());
        }
        if (needReplace) {
            //清除对话框消失监听，防止某些app强制退出对话框
            dialog.setOnCancelListener(null);
            dialog.setOnDismissListener(null);
            LogUtil.d(">>>find", needReplace);
            //啥都不执行
            return null;
        }
        return XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
    }


}
