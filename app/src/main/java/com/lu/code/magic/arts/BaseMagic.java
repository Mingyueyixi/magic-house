package com.lu.code.magic.arts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;

public abstract class BaseMagic implements IXposedHookLoadPackage {
    private List<String> bindPackageNameList;

    public BaseMagic() {
        bindPackageNameList = new ArrayList<>();
    }

    public BaseMagic(String... bindPackageName) {
        bindPackageNameList = new ArrayList<>();
        bindPackageNameList.addAll(Arrays.asList(bindPackageName));
    }

    public List<String> getBindPackageNameList() {
        return bindPackageNameList;
    }

}
