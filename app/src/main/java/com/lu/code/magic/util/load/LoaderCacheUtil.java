package com.lu.code.magic.util.load;

import androidx.collection.LruCache;

public class LoaderCacheUtil {
    private static CacheObjectLoader cacheMemoryLoader = new CacheObjectLoader();
    private static SimpleImageLoader imageLoader = new SimpleImageLoader();

    public static CacheObjectLoader objectLoader() {
        return cacheMemoryLoader;
    }

    public static SimpleImageLoader imageLoader() {
        return imageLoader;
    }

    public static <E> CacheObjectLoader<E> newObjectLoader(LruCache<String,E> lruCache) {
        return new CacheObjectLoader((LruCache<String, Object>) lruCache);
    }


}
