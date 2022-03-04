package com.lu.code.foolish.egg.plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;

public abstract class BaseMagicPlugin implements IXposedHookLoadPackage {
    private List<String> bindPackageNameList;

    public BaseMagicPlugin() {
        bindPackageNameList = new ArrayList<>();
    }

    public BaseMagicPlugin(String... bindPackageName) {
        bindPackageNameList = new ArrayList<>();
        bindPackageNameList.addAll(Arrays.asList(bindPackageName));
    }

    public List<String> getBindPackageNameList() {
        return bindPackageNameList;
    }

}
