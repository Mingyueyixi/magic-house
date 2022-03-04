package com.lu.code.magic.arts;

import com.lu.code.magic.util.CollectionUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MagicRepository {
    private HashMap<String, List<BaseMagic>> mPluginRepoMap = new HashMap<>();
    private List<BaseMagic> mNoPackageRepoList = new ArrayList<>();

    private static final class Single {
        private final static MagicRepository sInstance = new MagicRepository();
    }

    public static MagicRepository getInstance() {
        return Single.sInstance;
    }

    public void add(BaseMagic... plugins) {
        if (CollectionUtil.isEmptyArray(plugins)) {
            return;
        }
        for (BaseMagic plugin : plugins) {
            List<String> packageNameList = plugin.getBindPackageNameList();
            if (packageNameList == null || packageNameList.size() == 0) {
                mNoPackageRepoList.add(plugin);
                continue;
            }
            for (String s : packageNameList) {
                List<BaseMagic> packagePluginList = mPluginRepoMap.get(s);
                if (packagePluginList == null) {
                    packagePluginList = new ArrayList<>();
                    mPluginRepoMap.put(s, packagePluginList);
                }
                packagePluginList.add(plugin);
            }
        }
    }

    public void remove(BaseMagic... plugins) {
        if (CollectionUtil.isEmptyArray(plugins)) {
            return;
        }
        Set<Map.Entry<String, List<BaseMagic>>> entrySet = mPluginRepoMap.entrySet();
        Iterator<Map.Entry<String, List<BaseMagic>>> it = entrySet.iterator();
        while (it.hasNext()) {
            Map.Entry<String, List<BaseMagic>> eleMap = it.next();
            List<BaseMagic> pluginList = eleMap.getValue();
            if (pluginList == null || pluginList.size() == 0) {
                continue;
            }
            for (BaseMagic plugin : plugins) {
                //移除掉
                CollectionUtil.removeByIterator(pluginList, plugin);
            }
        }
    }

    public void remove(String packageName) {
        mPluginRepoMap.remove(packageName);
    }

    public List<BaseMagic> get(String packageName) {
        return mPluginRepoMap.get(packageName);
    }

    public HashMap<String, List<BaseMagic>> getPluginRepoMap() {
        return mPluginRepoMap;
    }

    public List<BaseMagic> getNoPackageRepoList() {
        return mNoPackageRepoList;
    }

    public void clear() {
        mNoPackageRepoList.clear();
        mPluginRepoMap.clear();
    }

}
