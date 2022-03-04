package com.lu.code.foolish.egg.plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;

public abstract class BaseHookPlugin implements IXposedHookLoadPackage {
    private List<String> bindPackageNameList;

    public BaseHookPlugin() {
        bindPackageNameList = new ArrayList<>();
    }

    public BaseHookPlugin(String... bindPackageName) {
        bindPackageNameList = new ArrayList<>();
        bindPackageNameList.addAll(Arrays.asList(bindPackageName));
    }

    public List<String> getBindPackageNameList() {
        return bindPackageNameList;
    }

}
