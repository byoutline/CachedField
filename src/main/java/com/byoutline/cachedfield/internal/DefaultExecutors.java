package com.byoutline.cachedfield.internal;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public final class DefaultExecutors {

    public static Executor createDefaultStateListenerExecutor() {
        // Execute on same thread.
        return new Executor() {
            @Override
            public void execute(Runnable command) {
                command.run();
            }
        };
    }

    public static ExecutorService createDefaultValueGetterExecutor() {
        return Executors.newCachedThreadPool();
    }
}
