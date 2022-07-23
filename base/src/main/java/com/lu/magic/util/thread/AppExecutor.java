/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lu.magic.util.thread;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池工具类
 */
public class AppExecutor {
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE_SECONDS = 30;

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "AppExecutor@IO #" + mCount.getAndIncrement());
        }
    };

    //放在最后初始化
    private static final Manager MANAGER = new Manager();

    private static class Manager {
        private static final int THREAD_COUNT = 3;
        private final Executor diskIO;
        private final Executor mainThread;
        private final Executor singleThread;
        private final Executor newThread;
        private final Executor io;

        public Manager() {
            this.diskIO = new DiskIOThreadExecutor();
            this.mainThread = new MainThreadExecutor();
            this.singleThread = Executors.newSingleThreadExecutor();
            this.newThread = new NewThreadExecutor();
            this.io = new ThreadPoolExecutor(
                    CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<Runnable>(128), sThreadFactory);
        }

    }

    public static Executor diskIO() {
        return MANAGER.diskIO;
    }

    public static Executor mainThread() {
        return MANAGER.mainThread;
    }

    public static Executor singleThread() {
        return MANAGER.singleThread;
    }

    public static Executor newThread() {
        return MANAGER.newThread;
    }

    public static Executor io() {
        return MANAGER.io;
    }

    public static void executeSingle(Runnable runnable) {
        MANAGER.singleThread.execute(runnable);
    }

    public static void executeMain(Runnable runnable) {
        MANAGER.mainThread.execute(runnable);
    }

    public static void executeDiskIO(Runnable runnable) {
        MANAGER.diskIO.execute(runnable);
    }

    public static void executeIO(Runnable runnable) {
        MANAGER.io.execute(runnable);
    }

    public static void executeNewThread(Runnable runnable) {
        MANAGER.newThread.execute(runnable);
    }

    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }

    private static class DiskIOThreadExecutor implements Executor {

        private final Executor mDiskIO;

        public DiskIOThreadExecutor() {
            mDiskIO = Executors.newSingleThreadExecutor();
        }

        @Override
        public void execute(@NonNull Runnable command) {
            mDiskIO.execute(command);
        }
    }

    private static class NewThreadExecutor implements Executor {
        @Override
        public void execute(Runnable command) {
            new Thread(command).start();
        }
    }

}
