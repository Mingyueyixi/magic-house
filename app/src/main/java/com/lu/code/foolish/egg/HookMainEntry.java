package com.lu.code.foolish.egg;

import android.app.Application;
import android.content.Context;

import androidx.annotation.MainThread;

import com.lu.code.foolish.egg.hook.BaseHookPlugin;
import com.lu.code.foolish.egg.hook.DisableFlagSecurePlugin;
import com.lu.code.foolish.egg.hook.FuckDialogPlugin;
import com.lu.code.foolish.egg.hook.PluginRepository;
import com.lu.code.foolish.egg.hook.TestHookPlugin;
import com.lu.code.foolish.egg.util.AppUtil;
import com.lu.code.foolish.egg.util.CollectionUtil;
import com.lu.code.foolish.egg.util.log.LogUtil;

import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * @Author: Lu
 * Date: 2022/02/18
 * Description: kook 入口
 */
public class HookMainEntry implements IXposedHookLoadPackage {
    private static PluginRepository repository = PluginRepository.getInstance();

    static {
        repository.add(new HookSelfEntry());
        repository.add(new DisableFlagSecurePlugin());
        repository.add(new TestHookPlugin());
        repository.add(new FuckDialogPlugin());
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName.equals(BuildConfig.APPLICATION_ID)) {
            dispatchSelfHookPlugins(lpparam);
            return;
        }
        if (AppUtil.getInstance().getAppContext() == null) {
            AppUtil.init();
//            if (AppUtil.getInstance().getAppContext() == null) {
//                XposedHelpers.findAndHookMethod(Application.class,
//                        "onCreate",
//                        new XC_MethodHook() {
//                            @Override
//                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                                Context context = (Context) param.thisObject;
//                                AppUtil.getInstance().attachContext(context.getApplicationContext());
//                                LogUtil.d("是空的hook", lpparam.packageName, lpparam.processName);
//                            }
//                        });
//            } else {
//            }
            dispatchHookPlugins(lpparam);
            LogUtil.d("是空的2", lpparam.packageName, lpparam.processName);
        } else {
            LogUtil.d("不是空的", lpparam.packageName, lpparam.processName);
            dispatchHookPlugins(lpparam);
        }
    }

    private void dispatchSelfHookPlugins(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        List<BaseHookPlugin> plugins = repository.get(lpparam.packageName);
        if (!CollectionUtil.isEmpty(plugins)) {
            for (BaseHookPlugin plugin : plugins) {
                plugin.handleLoadPackage(lpparam);
            }
        }
    }

    private void dispatchHookPlugins(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        List<BaseHookPlugin> noPackagePlugins = repository.getNoPackageRepoList();
        if (!CollectionUtil.isEmpty(noPackagePlugins)) {
            for (BaseHookPlugin noPackagePlugin : noPackagePlugins) {
                LogUtil.d("handle plugin:", noPackagePlugin.getClass(), lpparam.packageName);
                noPackagePlugin.handleLoadPackage(lpparam);
            }
        }


        List<BaseHookPlugin> registerPlugins = repository.get(lpparam.packageName);
        if (!CollectionUtil.isEmpty(registerPlugins)) {
            for (BaseHookPlugin plugin : registerPlugins) {
                LogUtil.d("handle plugin:", plugin.getClass(), lpparam.packageName);
                plugin.handleLoadPackage(lpparam);
            }
        }
    }

}
