package com.lu.code.magic.arts;

import de.robv.android.xposed.IXposedHookLoadPackage;

public abstract class BaseMagic implements IXposedHookLoadPackage {

    public BaseMagic() {

    }

    public String getMagicId() {
        return getMagicId(getClass());
    }

    public static <T extends BaseMagic> String getMagicId(Class<T> aClass) {
        return aClass.getName();
    }
}
