package com.lu.code.foolish.egg;

import com.lu.code.foolish.egg.plugin.BaseHookPlugin;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * @Author: Lu
 * Date: 2022/02/21
 * Description:
 */
public class HookSelfEntry extends BaseHookPlugin {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

    }
}
