package com.byoutline.ibuscachedfield.builders;

import com.byoutline.cachedfield.ProviderWithArg;
import com.byoutline.cachedfield.cachedendpoint.CachedEndpointWithArg;
import com.byoutline.cachedfield.cachedendpoint.StateAndValue;
import com.byoutline.ibuscachedfield.events.ResponseEventWithArg;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

import javax.inject.Provider;

/**
 * @param <RETURN_TYPE>     Type of object to be cached.
 * @param <ARG_TYPE>        Type of argument that needs to be passed to make an API call.
 * @param <BUS>             type of Bus that can be used to post events.
 * @param <CACHED_ENDPOINT> Specific type of {@link CachedEndpointWithArg} returned.
 */
public interface CachedEndpointWithArgConstructorWrapper<RETURN_TYPE, ARG_TYPE, BUS, CACHED_ENDPOINT extends CachedEndpointWithArg<RETURN_TYPE, ARG_TYPE>> {
    CACHED_ENDPOINT build(Provider<String> sessionIdProvider,
                          ProviderWithArg<RETURN_TYPE, ARG_TYPE> valueGetter,
                          ResponseEventWithArg<StateAndValue<RETURN_TYPE, ARG_TYPE>, ARG_TYPE> resultEvent,
                          BUS bus, ExecutorService valueGetterExecutor, Executor stateListenerExecutor);
}
