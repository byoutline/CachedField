package com.byoutline.ibuscachedfield.builders;

import com.byoutline.cachedfield.CachedFieldWithArg;
import com.byoutline.cachedfield.ProviderWithArg;
import com.byoutline.ibuscachedfield.events.ResponseEventWithArg;

import javax.inject.Provider;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

/**
 * @param <RETURN_TYPE>  Type of object to be cached.
 * @param <ARG_TYPE>     Type of argument that needs to be passed to calculate value.
 * @param <BUS>          type of Bus that can be used to post events.
 *                       {@link CachedFieldWithArg} build.
 * @param <CACHED_FIELD> Specific type of {@link CachedFieldWithArg} returned.
 */
public interface CachedFieldWithArgConstructorWrapper
        <RETURN_TYPE, ARG_TYPE, BUS, CACHED_FIELD extends CachedFieldWithArg<RETURN_TYPE, ARG_TYPE>> {

    CACHED_FIELD build(Provider<String> sessionIdProvider,
                       ProviderWithArg<RETURN_TYPE, ARG_TYPE> valueGetter,
                       ResponseEventWithArg<RETURN_TYPE, ARG_TYPE> successEvent,
                       ResponseEventWithArg<Exception, ARG_TYPE> errorEvent,
                       BUS bus,
                       ExecutorService valueGetterExecutor, Executor stateListenerExecutor);
}
