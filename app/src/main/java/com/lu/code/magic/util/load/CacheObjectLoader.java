package com.lu.code.magic.util.load;

import androidx.collection.LruCache;

public class CacheObjectLoader<E> {
    private MemoryRepositoryImp<E> mCachePoolImp;

    public CacheObjectLoader() {
        this(new LruCache<>(4096));
    }

    public CacheObjectLoader(LruCache<String, E> pools) {
        mCachePoolImp = new MemoryRepositoryImp<>(pools);
    }

    public ILoadWorker<E> with() {
        return new MemoryLoaderWorker<>(mCachePoolImp);
    }

}
