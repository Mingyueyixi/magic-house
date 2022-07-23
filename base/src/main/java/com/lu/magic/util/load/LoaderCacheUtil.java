package com.lu.magic.util.load;

import androidx.collection.LruCache;
import androidx.core.util.Supplier;

import com.lu.magic.util.thread.AppExecutor;
import com.lu.magic.util.thread.WorkerUtil;

import java.util.concurrent.Executor;

public class LoaderCacheUtil {

    public static class Manager {
        private static ObjectLoader<Object> objectLoader = new ObjectLoader<>(new LruCache<>(1024));
    }

    public static <E> ObjectLoader<E> createObjectLoader(LruCache<String, E> lruCache) {
        return new ObjectLoader<E>(lruCache);
    }

    public static ObjectLoader<Object> objectLoader() {
        return Manager.objectLoader;
    }

    public static class ObjectLoader<E> {
        private LruCache<String, E> cache;

        ObjectLoader(LruCache<String, E> lruCache) {
            this.cache = lruCache;
        }

        public LruCache<String, E> getCache() {
            return cache;
        }

        public ObjectLoadWorker<E> loadOn(Executor executor, String key, Supplier<E> supplier) {
            return new ObjectLoadWorker<E>(executor, this, key);
        }

        public WorkerUtil.ILoaderTarget<E> loadIO(String key, Supplier<E> supplier) {
            return new ObjectLoadWorker<E>(AppExecutor.io(), this, key).doOn(supplier);
        }

        public WorkerUtil.ILoaderTarget<E> loadSingle(String key, Supplier<E> supplier) {
            return new ObjectLoadWorker<E>(AppExecutor.singleThread(), this, key).doOn(supplier);
        }

        public WorkerUtil.ILoaderTarget<E> loadNewThread(String key, Supplier<E> supplier) {
            return new ObjectLoadWorker<E>(AppExecutor.singleThread(), this, key).doOn(supplier);
        }

    }


    public static class SelfObjectLoadWorker<E> implements ILoadWorker<E> {
        private final Executor mExecutor;
        private String mObjectKey;
        private ObjectLoader mObjectLoader;

        public SelfObjectLoadWorker(Executor executor, ObjectLoader objectLoader, String key) {
            this.mExecutor = executor;
            this.mObjectLoader = objectLoader;
            this.mObjectKey = key;
        }

        @Override
        public ILoaderTarget<E> doOn(Supplier<E> supplier) {
            return new ILoaderTarget<E>() {
                @Override
                public void into(Executor executor, ILoadTargetInto<E> loadTargetInto) {
                    loadTargetInto.onStart();
                    mExecutor.execute(() -> {
                        E result = supplier.get();
                        mObjectLoader.cache.put(mObjectKey, result);
                        if (mExecutor == executor) {
                            loadTargetInto.onComplete(result);
                        } else {
                            executor.execute(() -> loadTargetInto.onComplete(result));
                        }
                    });
                }

                @Override
                public void intoMain(ILoadTargetInto<E> loadTargetInto) {
                    into(AppExecutor.mainThread(), loadTargetInto);
                }

                @Override
                public void into(ILoadTargetInto<E> loadTargetInto) {
                    into(mExecutor, loadTargetInto);
                }

            };
        }

    }


    public interface ILoaderTarget<E> {
        void into(Executor executor, ILoadTargetInto<E> consumer);

        void intoMain(ILoadTargetInto<E> consumer);

        void into(ILoadTargetInto<E> consumer);
    }

    public interface ILoadWorker<E> {
        ILoaderTarget<E> doOn(Supplier<E> supplier);
    }

}
