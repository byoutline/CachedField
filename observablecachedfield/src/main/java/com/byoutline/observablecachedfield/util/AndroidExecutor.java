package com.byoutline.observablecachedfield.util;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;

import javax.annotation.Nonnull;

public class AndroidExecutor {
    private static final Handler HANDLER = new Handler(Looper.getMainLooper());
    public static final Executor MAIN_THREAD_EXECUTOR = new Executor() {
        @Override
        public void execute(@Nonnull Runnable runnable) {
            runInMainThread(runnable);
        }
    };

    public static void runInMainThread(Runnable runnable) {
        HANDLER.post(runnable);
    }
}
