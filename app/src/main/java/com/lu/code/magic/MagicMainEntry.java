package com.lu.code.magic;

import android.app.Application;
import android.content.Context;

import com.lu.code.magic.arts.BaseMagic;
import com.lu.code.magic.arts.DisableFlagSecureMagic;
import com.lu.code.magic.arts.FuckAMapLocationMagic;
import com.lu.code.magic.arts.FuckDialogMagic;
import com.lu.code.magic.arts.FuckVibratorMagic;
import com.lu.code.magic.arts.LocationMagic;
import com.lu.code.magic.arts.MagicRepository;
import com.lu.code.magic.arts.TestMagic;
import com.lu.code.magic.magic.BuildConfig;
import com.lu.code.magic.util.AppUtil;
import com.lu.code.magic.util.CollectionUtil;
import com.lu.code.magic.util.log.LogUtil;

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
public class MagicMainEntry implements IXposedHookLoadPackage {
    private static MagicRepository repository = MagicRepository.getInstance();

    static {
        repository.add(new MagicSelfEntry());
        repository.add(new DisableFlagSecureMagic());
        repository.add(new TestMagic());
        repository.add(new FuckDialogMagic());
        repository.add(new LocationMagic());
        repository.add(new FuckAMapLocationMagic());
        repository.add(new FuckVibratorMagic());
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        if (lpparam.packageName.equals(BuildConfig.APPLICATION_ID)) {
            dispatchSelfHookPlugins(lpparam);
            return;
        }
        if (AppUtil.hasInit()) {
            LogUtil.d("已经初始化", lpparam.packageName, lpparam.processName);
            dispatchHookPlugins(lpparam);
        } else {
            LogUtil.d("准备初始化，获取context", lpparam.packageName, lpparam.processName);
            Application app = AppUtil.getApplicationByReflect();
            if (app != null) {
                AppUtil.doInit(app);
                return;
            }
            LogUtil.d("通过Application获取Context", lpparam.packageName, lpparam.processName);
            XposedHelpers.findAndHookMethod(Application.class,
                    "onCreate",
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            Context context = (Context) param.thisObject;
                            AppUtil.doInit(context.getApplicationContext());
                            dispatchHookPlugins(lpparam);
                        }
                    });
        }
    }

    private void dispatchSelfHookPlugins(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        List<BaseMagic> plugins = repository.get(lpparam.packageName);
        if (!CollectionUtil.isEmpty(plugins)) {
            for (BaseMagic plugin : plugins) {
                plugin.handleLoadPackage(lpparam);
            }
        }
    }

    private void dispatchHookPlugins(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        List<BaseMagic> noPackagePlugins = repository.getNoPackageRepoList();
        if (!CollectionUtil.isEmpty(noPackagePlugins)) {
            for (BaseMagic noPackagePlugin : noPackagePlugins) {
                LogUtil.d("handle plugin:", noPackagePlugin.getClass(), lpparam.processName);
                noPackagePlugin.handleLoadPackage(lpparam);
            }
        }


        List<BaseMagic> registerPlugins = repository.get(lpparam.packageName);
        if (!CollectionUtil.isEmpty(registerPlugins)) {
            for (BaseMagic plugin : registerPlugins) {
                LogUtil.d("handle plugin:", plugin.getClass(), lpparam.processName);
                plugin.handleLoadPackage(lpparam);
            }
        }
    }

}
