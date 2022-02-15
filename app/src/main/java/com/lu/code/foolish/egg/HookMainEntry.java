package com.lu.code.foolish.egg;

import com.lu.code.foolish.egg.hook.BaseHookPlugin;
import com.lu.code.foolish.egg.hook.DisableFlagSecurePlugin;
import com.lu.code.foolish.egg.hook.PluginRepository;

import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookMainEntry implements IXposedHookLoadPackage {
    private static PluginRepository repository = PluginRepository.getInstance();

    static {
        repository.add(new DisableFlagSecurePlugin());
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        List<BaseHookPlugin> plugins = repository.get(lpparam.packageName);
        for (BaseHookPlugin plugin : plugins) {
            plugin.handleLoadPackage(lpparam);
        }
    }

}
