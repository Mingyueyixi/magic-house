package com.lu.magic.util.thread;

import androidx.core.util.Consumer;
import androidx.core.util.Supplier;


import java.util.concurrent.Executor;

public class WorkerUtil {

    public static <E> ILoaderTarget<E> loadOn(Executor executor, Supplier<E> supplier) {
        return new LoadWorker(executor).doOn(supplier);
    }

    public static <E> ILoaderTarget<E> loadSingle(Supplier<E> supplier) {
        return new LoadWorker(AppExecutor.singleThread()).doOn(supplier);
    }

    public static <E> ILoaderTarget<E> loadNewThread(Supplier<E> supplier) {
        return new LoadWorker(AppExecutor.newThread()).doOn(supplier);
    }

    public static <E> ILoaderTarget<E> loadIO(Supplier<E> supplier) {
        return new LoadWorker(AppExecutor.io()).doOn(supplier);
    }

    public static void execute(Executor executor, Runnable runnable) {
        executor.execute(runnable);
    }

    public static void executeIO(Runnable runnable) {
        AppExecutor.executeIO(runnable);
    }

    public static void executeSingle(Runnable runnable) {
        AppExecutor.executeSingle(runnable);
    }

    public static void executeNewThread(Runnable runnable) {
        AppExecutor.executeNewThread(runnable);
    }


    public static class LoadWorker<E> implements ILoadWorker<E> {
        protected Executor mExecutor;

        public LoadWorker(Executor executor) {
            mExecutor = executor;
        }

        @Override
        public ILoaderTarget<E> doOn(Supplier<E> supplier) {
            return new ILoaderTarget<E>() {
                @Override
                public void into(Executor executor, Consumer<E> consumer) {
                    if (executor == mExecutor) {
                        mExecutor.execute(() -> {
                            E result = supplier.get();
                            consumer.accept(result);
                        });
                        return;
                    }
                    mExecutor.execute(() -> {
                        E result = supplier.get();
                        executor.execute(() -> consumer.accept(result));
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


    public interface ILoaderTarget<E> {
        void into(Executor executor, Consumer<E> consumer);

        void intoMain(Consumer<E> consumer);

        void into(Consumer<E> consumer);
    }

    public interface ILoadWorker<E> {
        ILoaderTarget<E> doOn(Supplier<E> supplier);
    }

//    public interface IExecuteWorker {
//        void doOn(Runnable runnable);
//    }
}
