package com.lu.code.magic;

import android.app.Application;
import android.content.Context;

import com.lu.code.magic.arts.BaseMagic;
import com.lu.code.magic.arts.DisableFlagSecureMagic;
import com.lu.code.magic.arts.FuckAMapLocationMagic;
import com.lu.code.magic.arts.FuckDialogMagic;
import com.lu.code.magic.arts.FuckScreenMagic;
import com.lu.code.magic.arts.FuckVibratorMagic;
import com.lu.code.magic.arts.LocationMagic;
import com.lu.code.magic.arts.MagicRepository;
import com.lu.code.magic.arts.TestMagic;
import com.lu.code.magic.magic.BuildConfig;
import com.lu.code.magic.util.AppUtil;
import com.lu.code.magic.util.log.LogUtil;

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
        repository.add(new FuckDialogMagic());
        repository.add(new LocationMagic());
        repository.add(new FuckAMapLocationMagic());
        repository.add(new FuckVibratorMagic());
        repository.add(new FuckScreenMagic());
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName.equals(BuildConfig.APPLICATION_ID)) {
//            dispatchMagics(lpparam);
            return;
        }
        if (AppUtil.hasInit()) {
            LogUtil.d("已经初始化", lpparam.packageName, lpparam.processName, lpparam.isFirstApplication, lpparam.appInfo.uid);
            dispatchMagics(lpparam);
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
