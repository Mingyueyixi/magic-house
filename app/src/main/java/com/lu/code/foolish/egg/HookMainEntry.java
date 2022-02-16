package com.lu.code.foolish.egg;

import com.lu.code.foolish.egg.hook.BaseHookPlugin;
import com.lu.code.foolish.egg.hook.DisableFlagSecurePlugin;
import com.lu.code.foolish.egg.hook.PluginRepository;
import com.lu.code.foolish.egg.hook.TestHookPlugin;
import com.lu.code.foolish.egg.util.CollectionUtil;

import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookMainEntry implements IXposedHookLoadPackage {
    private static PluginRepository repository = PluginRepository.getInstance();

    static {
        repository.add(new DisableFlagSecurePlugin());
        repository.add(new TestHookPlugin());
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        List<BaseHookPlugin> noPackagePlugins = repository.getNoPackageRepoList();
        if (!CollectionUtil.isEmpty(noPackagePlugins)) {
            for (BaseHookPlugin noPackagePlugin : noPackagePlugins) {
                noPackagePlugin.handleLoadPackage(lpparam);
            }
        }
        List<BaseHookPlugin> registerPlugins = repository.get(lpparam.packageName);
        if (!CollectionUtil.isEmpty(registerPlugins)) {
            for (BaseHookPlugin plugin : registerPlugins) {
                plugin.handleLoadPackage(lpparam);
            }
        }
    }

}
