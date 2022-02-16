package com.lu.code.foolish.egg.hook;

import com.lu.code.foolish.egg.util.CollectionUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PluginRepository {
    private HashMap<String, List<BaseHookPlugin>> mPluginRepoMap = new HashMap<>();
    private List<BaseHookPlugin> mNoPackageRepoList = new ArrayList<>();

    private static final class Single {
        private final static PluginRepository sInstance = new PluginRepository();
    }

    public static PluginRepository getInstance() {
        return Single.sInstance;
    }

    public void add(BaseHookPlugin... plugins) {
        if (CollectionUtil.isEmptyArray(plugins)) {
            return;
        }
        for (BaseHookPlugin plugin : plugins) {
            List<String> packageNameList = plugin.getBindPackageNameList();
            if (packageNameList == null || packageNameList.size() == 0) {
                mNoPackageRepoList.add(plugin);
                continue;
            }
            for (String s : packageNameList) {
                List<BaseHookPlugin> packagePluginList = mPluginRepoMap.get(s);
                if (packagePluginList == null) {
                    packagePluginList = new ArrayList<>();
                    mPluginRepoMap.put(s, packagePluginList);
                }
                packagePluginList.add(plugin);
            }
        }
    }

    public void remove(BaseHookPlugin... plugins) {
        if (CollectionUtil.isEmptyArray(plugins)) {
            return;
        }
        Set<Map.Entry<String, List<BaseHookPlugin>>> entrySet = mPluginRepoMap.entrySet();
        Iterator<Map.Entry<String, List<BaseHookPlugin>>> it = entrySet.iterator();
        while (it.hasNext()) {
            Map.Entry<String, List<BaseHookPlugin>> eleMap = it.next();
            List<BaseHookPlugin> pluginList = eleMap.getValue();
            if (pluginList == null || pluginList.size() == 0) {
                continue;
            }
            for (BaseHookPlugin plugin : plugins) {
                //移除掉
                CollectionUtil.removeByIterator(pluginList, plugin);
            }
        }
    }

    public void remove(String packageName) {
        mPluginRepoMap.remove(packageName);
    }

    public List<BaseHookPlugin> get(String packageName) {
        return mPluginRepoMap.get(packageName);
    }

    public HashMap<String, List<BaseHookPlugin>> getPluginRepoMap() {
        return mPluginRepoMap;
    }

    public List<BaseHookPlugin> getNoPackageRepoList() {
        return mNoPackageRepoList;
    }

    public void clear() {
        mNoPackageRepoList.clear();
        mPluginRepoMap.clear();
    }

}
