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
class LoadThread<RETURN_TYPE, ARG_TYPE> extends Thread {
    private final ProviderWithArg<RETURN_TYPE, ARG_TYPE> valueGetter;
    private final SuccessListenerWithArg<RETURN_TYPE, ARG_TYPE> successListener;
    private final ErrorListenerWithArg<ARG_TYPE> errorListener;
    private final CachedValue<RETURN_TYPE, ARG_TYPE> value;
    private final ARG_TYPE arg;
    private final boolean dropValueOnFailure;

    /**
     * Guards against posting two values from thread (for example calling success listener,
     * then getting interrupted and informing error listener).
     */
    private final AtomicBoolean resultPosted = new AtomicBoolean(false);

    public LoadThread(@Nonnull ProviderWithArg<RETURN_TYPE, ARG_TYPE> valueGetter,
                      @Nonnull SuccessListenerWithArg<RETURN_TYPE, ARG_TYPE> successListener,
                      @Nonnull ErrorListenerWithArg<ARG_TYPE> errorListener,
                      @Nonnull CachedValue<RETURN_TYPE, ARG_TYPE> value,
                      @Nullable ARG_TYPE arg,
                      boolean dropValueOnFailure) {
        this.valueGetter = valueGetter;
        this.successListener = successListener;
        this.errorListener = errorListener;
        this.value = value;
        this.arg = arg;
        this.dropValueOnFailure = dropValueOnFailure;
    }

    @Override
    public void run() {
        try {
            value.loadingStarted(arg);
            RETURN_TYPE fetchedValue = valueGetter.get(arg);
            // We want to successfully inform either success or error listener.
            // If success listener crashes we try to inform error listener.
            // If error listener crashes, then there is nothing left to inform.
            if (resultPosted.compareAndSet(false, true)) {
                try {
                    successListener.valueLoaded(fetchedValue, arg);
                } catch (Exception ex) {
                    forcePostError(ex);
                }
                value.setSuccess(fetchedValue, arg);
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
            forcePostError(ex);
        }
    }

    private void forcePostError(Exception ex) {
        value.setFailure(ex, arg);
        errorListener.valueLoadingFailed(ex, arg);
        if (dropValueOnFailure) {
            value.drop();
        }
    }
}
