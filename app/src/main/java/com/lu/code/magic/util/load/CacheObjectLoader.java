package com.lu.code.magic.util.load;

import androidx.collection.LruCache;

public class CacheObjectLoader {
    private MemoryRepositoryImp mCachePoolImp;

    public CacheObjectLoader() {
        this(new LruCache<>(4096));
    }

    public CacheObjectLoader(LruCache<String, Object> pools) {
        mCachePoolImp = new MemoryRepositoryImp<Object>(pools);
    }

    public <E> ILoadWorker<E> with() {
        return new MemoryLoaderWorker<>(mCachePoolImp);
    }

}
