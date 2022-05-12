package com.lu.code.magic.arts;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.lu.code.magic.bean.ViewLockConfig;
import com.lu.code.magic.util.TextUtil;
import com.lu.code.magic.util.ToastUtil;
import com.lu.code.magic.util.config.ConfigUtil;
import com.lu.code.magic.util.config.SheetName;
import com.lu.code.magic.util.dialog.DialogUtil;
import com.lu.code.magic.util.dialog.EditDialog;
import com.lu.code.magic.util.log.LogUtil;
import com.lu.code.magic.util.view.ViewUtil;

import java.util.regex.Pattern;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class ViewLockMagic extends BaseMagic {
    private ViewLockConfig config;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (config == null) {
            config = ConfigUtil.getCell(SheetName.VIEW_LOCK, lpparam.packageName, ViewLockConfig.class);
        }
        if (config == null) {
            return;
        }

        if (config.getData() == null) {
            return;
        }
        if (config.getData().lockAll) {
            //直接锁定所有
            handleLockAll(lpparam);
        } else {
            //关键字锁定
            if (config.getData().deepCheck) {
                handleKeyWordLockDeep(lpparam);
            } else {
                handleKeyWordLockSelf(lpparam);
            }
        }
    }

    private void handleLockAll(XC_LoadPackage.LoadPackageParam lpparam) {
        LogUtil.d("handleLockAll");
        XposedHelpers.findAndHookMethod(
                View.class,
                "performClick",
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        View view = (View) param.thisObject;
                        showInterceptDialog(view.getContext(), param);
//                        return XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
                        return null;
                    }

                }
        );

    }

    private void handleKeyWordLockSelf(XC_LoadPackage.LoadPackageParam lpparam) {
        XposedHelpers.findAndHookMethod(
                TextView.class,
                "performClick",
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        TextView view = (TextView) param.thisObject;
                        CharSequence text = view.getText();
                        if ("regex".equals(config.getData().kwMode)) {
                            int flag = Pattern.CASE_INSENSITIVE;
                            if (config.getData().matchDotLine) {
                                flag = Pattern.CASE_INSENSITIVE | Pattern.DOTALL;
                            }
                            if (TextUtil.find(config.getData().keyWord, text, flag)) {
                                showInterceptDialog(view.getContext(), param);
                                return null;
                            }
                        } else if (ViewUtil.textCheck().haveText(view, config.getData().keyWord)) {
                            showInterceptDialog(view.getContext(), param);
                            return null;
                        }
                        return XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
                    }


                }
        );

    }

    private void handleKeyWordLockDeep(XC_LoadPackage.LoadPackageParam lpparam) {
        XposedHelpers.findAndHookMethod(
                View.class,
                "performClick",
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        View view = (View) param.thisObject;
                        if ("regex".equals(config.getData().kwMode)) {
                            if (ViewUtil.textCheck().findText(view, config.getData().keyWord)) {

                            }
                        } else {
                            if (ViewUtil.textCheck().haveText(view, config.getData().keyWord)) {

                            }
                        }
                    }

                }
        );

    }

    private void showInterceptDialog(Context context, XC_MethodHook.MethodHookParam param) throws Throwable {
        EditDialog.Builder builder = DialogUtil.buildEditDialog(context);
        EditText editText = builder.getEditText();
        builder.setTitle(config.getData().title)
                .setContentHint(config.getData().mess)
                .setCancelable(false)
                .setNegativeButton("取消", (dialog, which) -> {
                    LogUtil.d("取消dialog");
                    dialog.dismiss();
                })
                .setPositiveButton("确定", (dialog, which) -> {
                    String inputText = editText.getText().toString();
                    if (TextUtil.isEmpty(inputText)) {
                        ToastUtil.show(context, "输入为空");
                        return;
                    }
                    if (inputText.equals(config.getData().password)) {
                        dialog.dismiss();
                        try {
                            XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        ToastUtil.show("验证失败");
                    }
                })
                .show();

    }


}
