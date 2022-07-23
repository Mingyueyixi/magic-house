package com.lu.magic.arts;

import android.app.Dialog;
import android.os.IBinder;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.PopupWindow;

import com.lu.magic.bean.FuckDialogConfig;
import com.lu.magic.util.GsonUtil;
import com.lu.magic.util.ToastUtil;
import com.lu.magic.util.config.ConfigUtil;
import com.lu.magic.util.log.LogUtil;
import com.lu.magic.util.view.ViewUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.regex.Pattern;

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
        //app可能存在多进程，从包名中找不到配置，于是取进程名配置。
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
        XposedHelpers.findAndHookMethod(Dialog.class,
                "show",
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        Dialog dialog = (Dialog) param.thisObject;
                        LogUtil.d(lpparam.processName, "will show a dialog", dialog);
                        Window window = dialog.getWindow();
                        ViewGroup rootView = (ViewGroup) window.getDecorView();
                        if (checkNeedHide(rootView)) {
                            //清除对话框消失监听，防止某些app强制退出应用
                            dialog.setOnCancelListener(null);
                            dialog.setOnDismissListener(null);
                            LogUtil.d("hide a dialog", dialog);
                            //返回空会替换掉函数，啥都不执行
                            return null;
                        }
                        strongHideDialogIfNeed(lpparam, param);
                        return XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
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
                        LogUtil.d(lpparam.processName, "PopupWindow will showAtLocation", param.args);
                        return replacePopupWindowShow(lpparam, param);
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
                        LogUtil.d(lpparam.processName, "PopupWindow will showAsDropDown", param.args);
                        return replacePopupWindowShow(lpparam, param);
                    }
                }
        );

    }

    private Object replacePopupWindowShow(XC_LoadPackage.LoadPackageParam lpparam, XC_MethodHook.MethodHookParam param) throws InvocationTargetException, IllegalAccessException {
        PopupWindow popupWindow = (PopupWindow) param.thisObject;
//        除了流氓app，不会有popupWindow在DecorView自行加入东西。取ContentView判断即可
//        不一定是ViewGroup，QQ就有是EditView的
        View contentView = popupWindow.getContentView();
        if (checkNeedHide(contentView)) {
            popupWindow.setOnDismissListener(null);
            //啥都不执行
            if (mFuckDialogConfig.isPromptTip()) {
                ToastUtil.show("hide a dialog");
            }
            LogUtil.d(lpparam.processName, "hide a PopupWindow", popupWindow);
            return null;
        }
        strongHidePopupWindowIfNeed(lpparam, param);
        return XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
    }

    private void strongHidePopupWindowIfNeed(XC_LoadPackage.LoadPackageParam lpackageParam, XC_MethodHook.MethodHookParam param) {
        if (!mFuckDialogConfig.isStrongHide()) {
            return;
        }
        PopupWindow popupWindow = (PopupWindow) param.thisObject;
        View contentView = popupWindow.getContentView();
        int idTagKey = contentView.getId();
        ViewTreeObserver.OnGlobalLayoutListener listener = (ViewTreeObserver.OnGlobalLayoutListener) contentView.getTag(idTagKey);
        if (listener == null) {
            listener = () -> {
                if (!checkNeedHide(contentView)) {
                    return;
                }
                popupWindow.setOnDismissListener(null);
                popupWindow.dismiss();
                if (mFuckDialogConfig.isPromptTip()) {
                    ToastUtil.show("hide a PopupWindow");
                }
                LogUtil.d(lpackageParam.processName, "strong hide a PopupWindow", popupWindow);
            };
        } else {
            contentView.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        }
        contentView.setTag(idTagKey, listener);
        contentView.getViewTreeObserver().addOnGlobalLayoutListener(listener);
    }

    private void strongHideDialogIfNeed(XC_LoadPackage.LoadPackageParam lpackageParam, XC_MethodHook.MethodHookParam param) {
        if (!mFuckDialogConfig.isStrongHide()) {
            return;
        }
        Dialog dialog = (Dialog) param.thisObject;
        Window window = dialog.getWindow();
        ViewGroup rootView = (ViewGroup) window.getDecorView();
        int idTagKey = rootView.getId();
        ViewTreeObserver.OnGlobalLayoutListener listener = (ViewTreeObserver.OnGlobalLayoutListener) rootView.getTag(idTagKey);
        rootView.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        if (listener == null) {
            listener = () -> {
                if (!checkNeedHide(rootView)) {
                    return;
                }
                dialog.setOnDismissListener(null);
                dialog.setOnCancelListener(null);
                dialog.dismiss();
                if (mFuckDialogConfig.isPromptTip()) {
                    ToastUtil.show("hide a dialog");
                }
                LogUtil.d(lpackageParam.processName, "strong hide a dialog", dialog);
            };
        } else {
            rootView.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        }

        rootView.setTag(idTagKey, listener);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(listener);
    }

    private boolean checkNeedHide(View view) {
        boolean needHide;
        if (view == null) {
            return false;
        }
        if ("regex".equals(mFuckDialogConfig.getMode())) {
            int flag = Pattern.CASE_INSENSITIVE;
            if (mFuckDialogConfig.getRegexMode().isDotLine()) {
                flag = Pattern.CASE_INSENSITIVE | Pattern.DOTALL;
            }
            needHide = ViewUtil.textCheck().findText(view, mFuckDialogConfig.getKeyword(), flag);
        } else {
            needHide = ViewUtil.textCheck().haveText(view, mFuckDialogConfig.getKeyword());
        }
        return needHide;
    }

}
