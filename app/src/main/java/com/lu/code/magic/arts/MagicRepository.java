package com.lu.code.magic.arts;

import com.lu.code.magic.util.CollectionUtil;

import java.util.LinkedHashMap;

public class MagicRepository {
    private LinkedHashMap<String, BaseMagic> mMagicRepoMap = new LinkedHashMap<>();

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
            mMagicRepoMap.put(plugin.getMagicId(), plugin);
        }
    }

    public void remove(BaseMagic... plugins) {
        if (CollectionUtil.isEmptyArray(plugins)) {
            return;
        }
        for (BaseMagic plugin : plugins) {
            mMagicRepoMap.remove(plugin.getMagicId());
        }
    }

    public void remove(String magicId) {
        mMagicRepoMap.remove(magicId);
    }

    public void remove(Class<BaseMagic> tClass) {
        String key = BaseMagic.getMagicId(tClass);
        remove(key);
    }

    public BaseMagic get(String magicId) {
        return mMagicRepoMap.get(magicId);
    }

    public BaseMagic get(Class<BaseMagic> tClass) {
        String key = BaseMagic.getMagicId(tClass);
        return get(key);
    }

    public void clear() {
        mMagicRepoMap.clear();
    }

    public LinkedHashMap<String, BaseMagic> getMagicRepoMap() {
        return mMagicRepoMap;
    }
}
