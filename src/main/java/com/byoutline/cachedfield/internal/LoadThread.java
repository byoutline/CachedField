package com.byoutline.cachedfield.internal;

import com.byoutline.cachedfield.ErrorListenerWithArg;
import com.byoutline.cachedfield.ProviderWithArg;
import com.byoutline.cachedfield.SuccessListenerWithArg;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Thread that loads {@link #valueGetter} into {@link #valueGetter} and informs listener.
 * <p>
 * After execution either {@link #successListener} or {@link #errorListener} will be informed
 * exactly once.
 * </p>
 *
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public class LoadThread<RETURN_TYPE, ARG_TYPE> extends Thread {
    private final ProviderWithArg<RETURN_TYPE, ARG_TYPE> valueGetter;
    private final SuccessListenerWithArg<RETURN_TYPE, ARG_TYPE> successListener;
    private final ErrorListenerWithArg<ARG_TYPE> errorListener;
    private final CachedValue<RETURN_TYPE, ARG_TYPE> value;
    private final ARG_TYPE arg;
    /**
     * Guards against posting two values from thread (for example calling success listener,
     * then getting interrupted and informing error listener).
     */
    private final AtomicBoolean resultPosted = new AtomicBoolean(false);

    public LoadThread(@Nonnull ProviderWithArg<RETURN_TYPE, ARG_TYPE> valueGetter,
                      @Nonnull SuccessListenerWithArg<RETURN_TYPE, ARG_TYPE> successListener,
                      @Nonnull ErrorListenerWithArg<ARG_TYPE> errorListener,
                      @Nonnull CachedValue<RETURN_TYPE, ARG_TYPE> value,
                      @Nullable ARG_TYPE arg) {
        this.valueGetter = valueGetter;
        this.successListener = successListener;
        this.errorListener = errorListener;
        this.value = value;
        this.arg = arg;
    }

    @Override
    public void run() {
        try {
            value.loadingStarted();
            RETURN_TYPE fetchedValue = valueGetter.get(arg);
            if (resultPosted.compareAndSet(false, true)) {
                successListener.valueLoaded(fetchedValue, arg);
                value.setValue(fetchedValue, arg);
            }
        } catch (Exception ex) {
            postError(ex);
        }
    }

    public void interruptAndInformListenersIfNeeded() {
        interrupt();
        postError(new InterruptedException("calculation interrupted by request with new argument"));
    }

    private void postError(Exception ex) {
        if (resultPosted.compareAndSet(false, true)) {
            errorListener.valueLoadingFailed(ex, arg);
            value.valueLoadingFailed();
        }
    }
}