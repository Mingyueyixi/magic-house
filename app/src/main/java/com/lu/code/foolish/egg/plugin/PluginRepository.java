package com.lu.code.foolish.egg.plugin;

import com.lu.code.foolish.egg.util.CollectionUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PluginRepository {
    private HashMap<String, List<BaseMagicPlugin>> mPluginRepoMap = new HashMap<>();
    private List<BaseMagicPlugin> mNoPackageRepoList = new ArrayList<>();

    private static final class Single {
        private final static PluginRepository sInstance = new PluginRepository();
    }

    public static PluginRepository getInstance() {
        return Single.sInstance;
    }

    public void add(BaseMagicPlugin... plugins) {
        if (CollectionUtil.isEmptyArray(plugins)) {
            return;
        }
        for (BaseMagicPlugin plugin : plugins) {
            List<String> packageNameList = plugin.getBindPackageNameList();
            if (packageNameList == null || packageNameList.size() == 0) {
                mNoPackageRepoList.add(plugin);
                continue;
            }
            for (String s : packageNameList) {
                List<BaseMagicPlugin> packagePluginList = mPluginRepoMap.get(s);
                if (packagePluginList == null) {
                    packagePluginList = new ArrayList<>();
                    mPluginRepoMap.put(s, packagePluginList);
                }
                packagePluginList.add(plugin);
            }
        }
    }

    public void remove(BaseMagicPlugin... plugins) {
        if (CollectionUtil.isEmptyArray(plugins)) {
            return;
        }
        Set<Map.Entry<String, List<BaseMagicPlugin>>> entrySet = mPluginRepoMap.entrySet();
        Iterator<Map.Entry<String, List<BaseMagicPlugin>>> it = entrySet.iterator();
        while (it.hasNext()) {
            Map.Entry<String, List<BaseMagicPlugin>> eleMap = it.next();
            List<BaseMagicPlugin> pluginList = eleMap.getValue();
            if (pluginList == null || pluginList.size() == 0) {
                continue;
            }
            for (BaseMagicPlugin plugin : plugins) {
                //移除掉
                CollectionUtil.removeByIterator(pluginList, plugin);
            }
        }
    }

    public void remove(String packageName) {
        mPluginRepoMap.remove(packageName);
    }

    public List<BaseMagicPlugin> get(String packageName) {
        return mPluginRepoMap.get(packageName);
    }

    public HashMap<String, List<BaseMagicPlugin>> getPluginRepoMap() {
        return mPluginRepoMap;
    }

    public List<BaseMagicPlugin> getNoPackageRepoList() {
        return mNoPackageRepoList;
    }

    public void clear() {
        mNoPackageRepoList.clear();
        mPluginRepoMap.clear();
    }

}
