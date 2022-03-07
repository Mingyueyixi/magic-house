package com.lu.code.magic.util.load;

public class LoaderCacheUtil {
    private static CacheObjectLoader cacheMemoryLoader = new CacheObjectLoader();
    private static SimpleImageLoader imageLoader = new SimpleImageLoader();

    public static CacheObjectLoader objectLoader() {
        return cacheMemoryLoader;
    }

    public static SimpleImageLoader imageLoader() {
        return imageLoader;
    }

    public static CacheObjectLoader newObjectLoader() {
        return new CacheObjectLoader();
    }


}
