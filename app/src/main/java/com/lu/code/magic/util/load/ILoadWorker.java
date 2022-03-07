package com.lu.code.magic.util.load;

import androidx.core.util.Supplier;

public interface ILoadWorker<E> {
    ILoadWorker<E> load(String key, Supplier<E > supplier);

    void into(LoadTarget<E> loadTarget);
}
