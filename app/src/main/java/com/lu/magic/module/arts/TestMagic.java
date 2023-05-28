package com.lu.magic.module.arts;

import android.view.View;
import android.widget.TextView;

import com.lu.magic.arts.BaseMagic;
import com.lu.magic.util.ReflectUtil;
import com.lu.magic.util.TextUtil;
import com.lu.magic.util.ToastUtil;
import com.lu.magic.util.log.LogUtil;
import com.lu.magic.util.view.SelfDeepCheck;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class TestMagic extends BaseMagic {
    private XSharedPreferences sp;
    private Map<String, ?> data;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XposedHelpers.findAndHookMethod(
                View.class,
                "performClick",
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        View view = (View) param.thisObject;
//                        if ("com.tencent.mm.ui.conversation.ConversationFolderItemView".equals(view.getClass())) {
//
//                        }
                        boolean has = findText(view, "屈凤文");
                        if (has) {
                            return null;
                        }
                        return XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);

                    }

                }
        );
    }

    private boolean findText(View rootView, String regex) {
        return new SelfDeepCheck().eachCheck(rootView, view -> {
            if (view instanceof TextView) {
                String input = ((TextView) view).getText() + "";
                return TextUtil.find(regex, input);
            } else {
                try {
                    Method m = ReflectUtil.getMatchingMethod(view.getClass(), "getText");
                    if (m != null) {
                        m.setAccessible(true);
                        Object v = m.invoke(view);
                        if (v != null) {
                            return TextUtil.find(regex, v + "");
                        }
                    }
                } catch (Exception e) {

                }
            }
            return false;
        });
    }

    private List<String> getTextList(View rootView) {
        List<String> stringList = new ArrayList<>();
        new SelfDeepCheck().each(rootView, view -> {
            if (view instanceof TextView) {
                stringList.add(((TextView) view).getText() + "");
            } else {
                try {
                    Method m = ReflectUtil.getMatchingMethod(view.getClass(), "getText");
                    if (m != null) {
                        m.setAccessible(true);
                        Object v = m.invoke(view);
                        stringList.add(v + "");
                        LogUtil.w("reflect ", view);
                    }
                } catch (Exception e) {

                }
            }
        });
        return stringList;
    }

    private Object proxy(XC_LoadPackage.LoadPackageParam lpparam, XC_MethodHook.MethodHookParam param) {
        Object sourceListener = param.args[0];
        if (Proxy.isProxyClass(sourceListener.getClass())) {
            return sourceListener;
        }
        return Proxy.newProxyInstance(sourceListener.getClass().getClassLoader(),
                sourceListener.getClass().getInterfaces(),
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        ToastUtil.show("尼玛的");
                        //原始方法
                        return method.invoke(sourceListener, args);
                    }
                }
        );
    }

    private void handlePanQie() {

    }


}
