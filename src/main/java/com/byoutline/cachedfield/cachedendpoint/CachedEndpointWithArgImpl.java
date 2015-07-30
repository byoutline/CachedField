package com.byoutline.cachedfield.cachedendpoint;

import com.byoutline.cachedfield.ProviderWithArg;
import com.byoutline.cachedfield.internal.CachedValue;
import com.byoutline.cachedfield.internal.StubErrorListenerWithArg;
import com.byoutline.cachedfield.internal.ValueLoader;
import com.byoutline.cachedfield.internal.cachedendpoint.StubSuccessListenerWithArg;

import javax.annotation.Nonnull;
import javax.inject.Provider;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

/**
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
                                     @Nonnull ExecutorService valueGetterExecutor,
                                     @Nonnull Executor stateListenerExecutor) {
        this.value = new CachedValue<RETURN_TYPE, ARG_TYPE>(sessionProvider, stateListenerExecutor);
        this.valueLoader = new ValueLoader<RETURN_TYPE, ARG_TYPE>(valueGetter,
                new StubSuccessListenerWithArg<RETURN_TYPE, ARG_TYPE>(),
                new StubErrorListenerWithArg<ARG_TYPE>(),
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
