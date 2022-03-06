package com.lu.code.magic.util.load;


import androidx.collection.LruCache;

public class MemoryRepositoryImp<E> {
    private LruCache<String, E> mCachePools;

    public MemoryRepositoryImp(LruCache<String, E> cachePools) {
        this.mCachePools = cachePools;
    }

    public void put(String key, E o) {
        mCachePools.put(key, o);
    }

    public void clear() {
        mCachePools.createCount();
    }

    public E get(String key) {
        return mCachePools.get(key);
    }

}