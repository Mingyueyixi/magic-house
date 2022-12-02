package com.lu.magic;

import android.app.Application;
import android.content.Context;

import com.lu.magic.arts.BaseMagic;
import com.lu.magic.arts.DisableFlagSecureMagic;
import com.lu.magic.arts.MagicRepository;
import com.lu.magic.arts.TestMagic;
import com.lu.magic.arts.ViewLockMagic;
import com.lu.magic.util.AppUtil;
import com.lu.magic.util.log.LogUtil;

import java.util.Map;

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
//        repository.add(new LocationMagic());
        repository.add(new ViewLockMagic());

        ModuleRegistry.INSTANCE.apply();
        for (Map.Entry<String, IModuleFace> ele : ModuleProviders.moduleFaces.entrySet()) {
            IModuleFace module = ele.getValue();
            BaseMagic magic = module.loadMagic();
            if (magic != null) {
                repository.add(magic);
            }
        }
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        try {
            handleMain(lpparam);
        } catch (Throwable throwable) {
            //避免崩溃
            LogUtil.e(throwable);
        }
    }

    private void handleMain(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName.equals(BuildConfig.APPLICATION_ID)) {
//            dispatchMagics(lpparam);
            return;
        }
        if (AppInitProxy.hasAttachContext()) {
            LogUtil.d("已经初始化", lpparam.packageName, lpparam.processName, lpparam.isFirstApplication, lpparam.appInfo.uid);
            dispatchMagics(lpparam);
        } else {
            LogUtil.d("准备初始化，获取context", lpparam.packageName, lpparam.processName);
            Application app = AppUtil.getApplicationByReflect();
            if (app != null) {
                AppInitProxy.callInit(app);
                return;
            }
            LogUtil.d("通过Application获取Context", lpparam.packageName, lpparam.processName);
            XposedHelpers.findAndHookMethod(Application.class,
                    "onCreate",
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            Context context = (Context) param.thisObject;
                            AppInitProxy.callInit(context);
                            dispatchMagics(lpparam);
                        }
                    });
        }
    }


    private void dispatchMagics(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        LogUtil.d("----", "apply magics for", lpparam.processName, "-----");
        for (Map.Entry<String, BaseMagic> entity : repository.getMagicRepoMap().entrySet()) {
            BaseMagic magic = entity.getValue();
            LogUtil.d("handle magic:", magic.getClass().getSimpleName());
            magic.handleLoadPackage(lpparam);
        }

    }

}
