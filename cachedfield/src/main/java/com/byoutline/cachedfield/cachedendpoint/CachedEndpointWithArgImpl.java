package com.byoutline.cachedfield.cachedendpoint;

import com.byoutline.cachedfield.ProviderWithArg;
import com.byoutline.cachedfield.internal.CachedValue;
import com.byoutline.cachedfield.internal.ValueLoader;
import com.byoutline.cachedfield.internal.cachedendpoint.CallEndWrapperWithArg;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

import javax.annotation.Nonnull;
import javax.inject.Provider;

/**
 * Wrapper for endpoint allowing executing calls from fragments/activities without leaking them.
 * <p>
 * Other than cache it also provides:
 * <ul>
 * <li>session checks(dropping cache if session was changed)</li>
 * <li>informing {@link EndpointStateListener} when call occurs which allows showing progress in ui</li>
 * <li>customizing {@link Executor}s for making call and informing state listeners for easier testing and
 * ui callbacks</li>
 * </ul>
 * </p>
 *
 * @param <RETURN_TYPE> Type of value to be cached
 * @param <ARG_TYPE>    Argument needed to calculate value.
 * @author Sebastian Kacprzak <nait at naitbit.com>
 */
public class CachedEndpointWithArgImpl<RETURN_TYPE, ARG_TYPE>
        implements CachedEndpointWithArg<RETURN_TYPE, ARG_TYPE> {

    private final CachedValue<RETURN_TYPE, ARG_TYPE> value;
    private final ValueLoader<RETURN_TYPE, ARG_TYPE> valueLoader;

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
                                     @Nonnull CallEndListener<RETURN_TYPE, ARG_TYPE> callEndListener,
                                     @Nonnull ExecutorService valueGetterExecutor,
                                     @Nonnull Executor stateListenerExecutor) {
        boolean informStateListenerOnAdd = true;
        this.value = new CachedValue<RETURN_TYPE, ARG_TYPE>(
                sessionProvider, stateListenerExecutor, informStateListenerOnAdd);
        CallEndWrapperWithArg<RETURN_TYPE, ARG_TYPE> resultListener = new CallEndWrapperWithArg<RETURN_TYPE, ARG_TYPE>(callEndListener);
        this.valueLoader = new ValueLoader<RETURN_TYPE, ARG_TYPE>(valueGetter,
                resultListener,
                resultListener,
                value, valueGetterExecutor, false);
    }


    @Override
    public void call(ARG_TYPE arg) {
        valueLoader.loadValue(arg);
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
