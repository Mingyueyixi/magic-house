package com.lu.magic.util.load;

import androidx.core.util.Consumer;
import androidx.core.util.Supplier;

import com.lu.magic.util.thread.AppExecutor;
import com.lu.magic.util.thread.WorkerUtil;

import java.util.concurrent.Executor;

public class ObjectLoadWorker<E> implements WorkerUtil.ILoadWorker<E> {
    private final Executor mExecutor;
    private String mObjectKey;
    private LoaderCacheUtil.ObjectLoader mObjectLoader;

    public ObjectLoadWorker(Executor executor, LoaderCacheUtil.ObjectLoader objectLoader, String key) {
        this.mExecutor = executor;
        this.mObjectLoader = objectLoader;
        this.mObjectKey = key;
    }

    @Override
    public WorkerUtil.ILoaderTarget<E> doOn(Supplier<E> supplier) {
        return new WorkerUtil.ILoaderTarget<E>() {
            @Override
            public void into(Executor executor, Consumer<E> consumer) {
                mExecutor.execute(() -> {
                    E result = supplier.get();
                    mObjectLoader.getCache().put(mObjectKey, result);

                    if (mExecutor == executor) {
                        consumer.accept(result);
                    } else {
                        executor.execute(() -> consumer.accept(result));
                    }
                });

            }

            @Override
            public void intoMain(Consumer<E> consumer) {
                into(AppExecutor.mainThread(), consumer);
            }

            @Override
            public void into(Consumer<E> consumer) {
                into(mExecutor, consumer);
            }
        };
    }

}
