package com.lu.magic.module.arts;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.lu.magic.arts.BaseMagic;
import com.lu.magic.module.App;
import com.lu.magic.util.AppUtil;
import com.lu.magic.util.ReflectUtil;
import com.lu.magic.util.TextUtil;
import com.lu.magic.util.ToastUtil;
import com.lu.magic.util.log.LogUtil;
import com.lu.magic.util.view.SelfDeepCheck;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Ref;
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
        LogUtil.i("test handleLoadPackage");
        Method execStartActivityMethod = ReflectUtil.getMatchingMethod(Instrumentation.class,
                "execStartActivity",
                Context.class,
                IBinder.class,
                IBinder.class,
                Activity.class,
                Intent.class,
                Integer.TYPE,
                Bundle.class);

        LogUtil.i("meeeeeeeeeeee: " + execStartActivityMethod.toString());
        XposedBridge.hookMethod(execStartActivityMethod, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                LogUtil.i("start activity 拦截");

                Intent intent = (Intent) param.args[4];
                String pkgName = AppUtil.getPackageName();

                Object act = intent.resolveActivity(AppUtil.getContext().getPackageManager());
                Object jumPkg = ReflectUtil.invokeMethod(act, "getPackageName");
                if (act != null && !pkgName.equals(jumPkg)) {
                    //跳转到其他app
                    LogUtil.w("Intent resolve null");
                    return null;
                }

                return XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
            }
        });

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
