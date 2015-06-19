package com.byoutline.cachedfield.internal;

import com.byoutline.cachedfield.ErrorListenerWithArg;
import com.byoutline.cachedfield.ProviderWithArg;
import com.byoutline.cachedfield.SuccessListenerWithArg;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Thread that loads {@link #valueGetter} into {@value} and informs listener.
 *
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public class LoadThread<RETURN_TYPE, ARG_TYPE> extends Thread {
    private final ProviderWithArg<RETURN_TYPE, ARG_TYPE> valueGetter;
    private final SuccessListenerWithArg<RETURN_TYPE, ARG_TYPE> successListener;
    private final ErrorListenerWithArg<ARG_TYPE> errorListener;
    private final CachedValue<RETURN_TYPE, ARG_TYPE> value;
    private final ARG_TYPE arg;

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
            System.out.println("run: " + arg);
            value.loadingStarted();
            RETURN_TYPE fetchedValue = valueGetter.get(arg);
            if (isInterrupted()) {
                throw getCancelException();
            }

            successListener.valueLoaded(fetchedValue, arg);
            value.setValue(fetchedValue, arg);
        } catch (Exception ex) {
            postError(ex);
        }
    }

    public void postCancellation() {
        postError(getCancelException());
    }

    private void postError(Exception ex) {
        errorListener.valueLoadingFailed(ex, arg);
        value.valueLoadingFailed();
    }

    private InterruptedException getCancelException() {
        return new InterruptedException("calculation interrupted by request with new argument");
    }
}
