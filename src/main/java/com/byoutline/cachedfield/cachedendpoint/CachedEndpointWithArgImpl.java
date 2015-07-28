package com.byoutline.cachedfield.cachedendpoint;

import com.byoutline.cachedfield.ProviderWithArg;
import com.byoutline.cachedfield.internal.CachedValue;
import com.byoutline.cachedfield.internal.LoadThread;
import com.byoutline.cachedfield.internal.StateAndValue;
import com.byoutline.cachedfield.internal.StubErrorListenerWithArg;

import javax.annotation.Nonnull;
import javax.inject.Provider;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * @param <RETURN_TYPE> Type of value to be cached
 * @param <ARG_TYPE>    Argument needed to calculate value.
 * @author Sebastian Kacprzak <nait at naitbit.com>
 */
public class CachedEndpointWithArgImpl<RETURN_TYPE, ARG_TYPE> implements CachedEndpointWithArg<RETURN_TYPE, ARG_TYPE> {

    private final ProviderWithArg<RETURN_TYPE, ARG_TYPE> valueGetter;
    private final CachedValue<RETURN_TYPE, ARG_TYPE> value;
    private final ExecutorService valueGetterExecutor;
    private LoadThread fetchThread;
    private Future fetchFuture;

    /**
     * @param sessionProvider       Provider that returns String unique for current
     *                              session. When session changes cached value will be dropped.
     * @param valueGetter           Provider that synchronously calculates/fetches value
     *                              and returns it.
     * @param valueGetterExecutor   ExecutorService that will be used to call valueGetter and call
     * @param stateListenerExecutor
     */
    public CachedEndpointWithArgImpl(@Nonnull Provider<String> sessionProvider,
                                     @Nonnull ProviderWithArg<RETURN_TYPE, ARG_TYPE> valueGetter,
                                     @Nonnull ExecutorService valueGetterExecutor,
                                     @Nonnull Executor stateListenerExecutor) {
        this.value = new CachedValue<RETURN_TYPE, ARG_TYPE>(sessionProvider, stateListenerExecutor);
        this.valueGetter = valueGetter;
        this.valueGetterExecutor = valueGetterExecutor;
    }


    @Override
    public void call(ARG_TYPE arg) {
        loadValue(arg);
    }

    /**
     * Loads value in separate thread.
     */
    private void loadValue(final ARG_TYPE arg) {
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
                new StubSuccessListenerWithArg<RETURN_TYPE, ARG_TYPE>(),
                new StubErrorListenerWithArg<ARG_TYPE>(),
                value, arg);
        fetchFuture = valueGetterExecutor.submit(fetchThread);
    }

    @Override
    public StateAndValue<RETURN_TYPE, ARG_TYPE> getStateAndValue() {
        return value.getStateAndValue();
    }

    @Override
    public void drop() {
        value.drop();
    }

    @Override
    public void addEndpointListener(@Nonnull EndpointStateListener<RETURN_TYPE, ARG_TYPE> listener) throws IllegalArgumentException {
        value.addStateListener(listener);
    }

    @Override
    public boolean removeEndpointListener(@Nonnull EndpointStateListener<RETURN_TYPE, ARG_TYPE> listener) throws IllegalArgumentException {
        return value.removeStateListener(listener);
    }

}
