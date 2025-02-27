package com.smart.rinoiot.common.utils;

import android.os.Handler;
import android.os.Looper;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 线程切换类
 *
 * @className AppExecutors
 * @date: 2020/6/3 10:57 AM
 * @author: xf
 */
public class AppExecutors {
    private static AppExecutors instance;
    private final Executor diskIO;

    private final Executor networkIO;

    private final Executor mainThread;

    private final ScheduledExecutorService delayedThread;

    public static AppExecutors getInstance() {
        if (instance == null) {
            instance = new AppExecutors();
        }
        return instance;
    }

    public AppExecutors(Executor diskIO, Executor networkIO, Executor mainThread,ScheduledExecutorService executorService) {
        this.diskIO = diskIO;
        this.networkIO = networkIO;
        this.mainThread = mainThread;
        this.delayedThread=executorService;
    }

    public AppExecutors() {
        this(Executors.newSingleThreadExecutor(), Executors.newFixedThreadPool(3),
                new MainThreadExecutor(),Executors.newScheduledThreadPool(40));
    }

    public ScheduledExecutorService delayedThread(){
        return delayedThread;
    }

    public Executor diskIO() {
        return diskIO;
    }

    public Executor networkIO() {
        return networkIO;
    }

    public Executor mainThread() {
        return mainThread;
    }

    public ScheduledExecutorService getDelayedThread() {
        return delayedThread;
    }

    private static class MainThreadExecutor implements Executor {
        private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NotNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}