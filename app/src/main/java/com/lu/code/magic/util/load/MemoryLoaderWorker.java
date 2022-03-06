package com.lu.code.magic.util.load;

import androidx.core.util.Supplier;

import com.lu.code.magic.util.thread.AppExecutor;

public class MemoryLoaderWorker<E> implements ILoadWorker<E> {
    private final MemoryRepositoryImp<E> mMemoryRepositoryImp;
    private String objectKey;
    private Supplier<E> supplier;
    private boolean loadWithSync;

    public MemoryLoaderWorker(MemoryRepositoryImp<E> loaderPool) {
        mMemoryRepositoryImp = loaderPool;
    }

    @Override
    public ILoadWorker<E> load(String key, Supplier<E> supplier) {
        return this.load(key, supplier, false);
    }

    public ILoadWorker<E> load(String key, Supplier<E> supplier, boolean sync) {
        this.objectKey = key;
        this.supplier = supplier;
        this.loadWithSync = sync;
        return this;
    }


    @Override
    public void into(LoadTarget loadTarget) {
        if (loadWithSync) {
            realLoadSync(loadTarget);
        } else {
            realLoad(loadTarget);
        }
    }

    private void realLoadSync(LoadTarget<E> loadTarget) {
        loadTarget.onStart();
        E object = mMemoryRepositoryImp.get(objectKey);
        if (object != null) {
            loadTarget.onComplete(object);
            return;
        }
        object = supplier.get();
        mMemoryRepositoryImp.put(objectKey, object);
        loadTarget.onComplete(object);
    }

    protected void realLoad(LoadTarget<E> loadTarget) {
        loadTarget.onStart();
        Object object = mMemoryRepositoryImp.get(objectKey);
        if (object != null) {
            loadTarget.onComplete((E) object);
            return;
        }
        AppExecutor.executeIO(() -> {
            E loadObject = supplier.get();
            AppExecutor.executeMain(() -> {
                mMemoryRepositoryImp.put(objectKey, loadObject);
                loadTarget.onComplete((E) loadObject);
            });
        });
    }

}
