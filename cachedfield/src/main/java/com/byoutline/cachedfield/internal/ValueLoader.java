package com.byoutline.cachedfield.internal;

import com.byoutline.cachedfield.ErrorListenerWithArg;
import com.byoutline.cachedfield.ProviderWithArg;
import com.byoutline.cachedfield.SuccessListenerWithArg;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.annotation.Nonnull;

/**
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public class ValueLoader<RETURN_TYPE, ARG_TYPE> {
    private final ProviderWithArg<RETURN_TYPE, ARG_TYPE> valueGetter;
    private final SuccessListenerWithArg<RETURN_TYPE, ARG_TYPE> successListener;
    private final ErrorListenerWithArg<ARG_TYPE> errorListener;
    private final CachedValue<RETURN_TYPE, ARG_TYPE> value;
    private final ExecutorService valueGetterExecutor;
    private final boolean dropValueOnFailure;
    private LoadThread fetchThread;
    private Future fetchFuture;

    public ValueLoader(@Nonnull ProviderWithArg<RETURN_TYPE, ARG_TYPE> valueGetter,
                       SuccessListenerWithArg<RETURN_TYPE, ARG_TYPE> successListener, ErrorListenerWithArg<ARG_TYPE> errorListener, @Nonnull CachedValue<RETURN_TYPE, ARG_TYPE> value,
                       @Nonnull ExecutorService valueGetterExecutor,
                       boolean dropValueOnFailure) {
        this.valueGetter = valueGetter;
        this.successListener = successListener;
        this.errorListener = errorListener;
        this.value = value;
        this.valueGetterExecutor = valueGetterExecutor;
        this.dropValueOnFailure = dropValueOnFailure;
    }

    /**
     * Loads value in separate thread.
     */
    public void loadValue(final ARG_TYPE arg) {
        if (fetchFuture != null) {
            // Cancel thread if it was not yet starter.
            fetchFuture.cancel(false);
            // If thread was cancelled before it was started inform error listeners.
            if (fetchThread != null) {
                fetchThread.interruptAndInformListenersIfNeeded();
            }
        }
        // We use thread instead of pure runnable so we can interrupt loading.
        fetchThread = new LoadThread<RETURN_TYPE, ARG_TYPE>(valueGetter,
                successListener,
                errorListener,
                value, arg, dropValueOnFailure);
        fetchFuture = valueGetterExecutor.submit(fetchThread);
    }
}
